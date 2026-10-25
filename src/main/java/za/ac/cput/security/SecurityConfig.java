package za.ac.cput.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter filter) throws Exception {
        return http.csrf(csrf->csrf.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->writeError(req,res,401,"Unauthorized","Authentication required"))
                .accessDeniedHandler((req,res,ex)->writeError(req,res,403,"Forbidden","Insufficient role")))
            .authorizeHttpRequests(a->a.requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/organizers/me/events").hasAnyRole("ORGANIZER","ADMIN").anyRequest().permitAll())
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class).build();
    }
    private static void writeError(jakarta.servlet.http.HttpServletRequest req,HttpServletResponse res,int status,String error,String message) throws java.io.IOException {
        res.setStatus(status);res.setContentType("application/json");new com.fasterxml.jackson.databind.ObjectMapper().writeValue(res.getOutputStream(),new za.ac.cput.dto.ApiErrorDTO(java.time.Instant.now().toString(),status,error,message,req.getRequestURI()));
    }
}
