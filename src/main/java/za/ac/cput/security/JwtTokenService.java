package za.ac.cput.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import za.ac.cput.domain.RoleEnum;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Component
public class JwtTokenService {
    private final ObjectMapper mapper;
    private final byte[] secret;
    public JwtTokenService(@Value("${univent.jwt.secret:development-only-change-this-secret-32-bytes}") String secret) {
        this.mapper=new ObjectMapper(); this.secret=secret.getBytes(StandardCharsets.UTF_8);
    }
    public String createToken(String userId, RoleEnum role) {
        try {
            Base64.Encoder enc=Base64.getUrlEncoder().withoutPadding();
            String header=enc.encodeToString(mapper.writeValueAsBytes(Map.of("alg","HS256","typ","JWT")));
            String payload=enc.encodeToString(mapper.writeValueAsBytes(Map.of("sub",userId,"role",role.name(),"exp",Instant.now().plusSeconds(3600).getEpochSecond())));
            String content=header+"."+payload;
            return content+"."+enc.encodeToString(sign(content));
        } catch (Exception e) { throw new IllegalStateException("Could not create token",e); }
    }
    public Map<String,Object> verify(String token) {
        try {
            String[] parts=token.split("\\.");
            if(parts.length!=3 || !java.security.MessageDigest.isEqual(sign(parts[0]+"."+parts[1]), Base64.getUrlDecoder().decode(parts[2]))) throw new IllegalArgumentException("Invalid token");
            Map<?,?> claims=mapper.readValue(Base64.getUrlDecoder().decode(parts[1]),Map.class);
            if(!(claims.get("sub") instanceof String id) || id.isBlank() || !(claims.get("role") instanceof String role)) throw new IllegalArgumentException("Invalid token claims");
            RoleEnum.valueOf(role);
            if(!(claims.get("exp") instanceof Number exp) || exp.longValue() <= Instant.now().getEpochSecond()) throw new IllegalArgumentException("Expired token");
            return Map.of("sub",id,"role",role);
        } catch (Exception e) { throw new IllegalArgumentException("Invalid bearer token",e); }
    }
    private byte[] sign(String value) throws Exception {
        Mac mac=Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(secret,"HmacSHA256"));
        return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }
}
