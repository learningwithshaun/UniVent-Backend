package za.ac.cput.security;

import za.ac.cput.domain.RoleEnum;

/** Login-free Bearer token helper for API tests. */
public final class JwtTestTokens {
    private JwtTestTokens() {}
    public static String bearer(JwtTokenService service, String userId, RoleEnum role) {
        return "Bearer " + service.createToken(userId, role);
    }
}
