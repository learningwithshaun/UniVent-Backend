package za.ac.cput.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService tokens;
    public JwtAuthenticationFilter(JwtTokenService tokens) { this.tokens=tokens; }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String auth=request.getHeader("Authorization");
        if(auth!=null && auth.startsWith("Bearer ")) {
            try {
                Map<String,Object> claims=tokens.verify(auth.substring(7));
                String role=(String)claims.get("role");
                var authentication=new UsernamePasswordAuthenticationToken(claims.get("sub"),null,List.of(new SimpleGrantedAuthority("ROLE_"+role)));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (IllegalArgumentException ex) {
                SecurityContextHolder.clearContext();
                response.setStatus(401); response.setContentType("application/json");
                new com.fasterxml.jackson.databind.ObjectMapper().writeValue(response.getOutputStream(),new za.ac.cput.dto.ApiErrorDTO(java.time.Instant.now().toString(),401,"Unauthorized","Invalid bearer token",request.getRequestURI())); return;
            }
        }
        chain.doFilter(request,response);
    }
}
