package za.ac.cput.security;

import za.ac.cput.domain.RoleEnum;

/**
 * Principal of the currently authenticated request.
 * Populated by the authentication layer (BE 01) until then, tests populate it directly.
 */
public record UserPrincipal(
        String userId,
        String email,
        RoleEnum role
) {
}
