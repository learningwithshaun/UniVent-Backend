package za.ac.cput.security;

import org.springframework.stereotype.Component;

/**
 * Thread-local implementation of {@link CurrentUserProvider}.
 * <p>
 * Intended as a placeholder until BE 01 installs JWT authentication, at which point
 * a JWT-backed implementation should replace this bean. Admin controllers only depend
 * on the {@link CurrentUserProvider} interface, so no controller change is needed then.
 */
@Component
public class ThreadLocalCurrentUserProvider implements CurrentUserProvider {

    private static final ThreadLocal<UserPrincipal> CURRENT_USER = new ThreadLocal<>();

    @Override
    public UserPrincipal getCurrentUser() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return CURRENT_USER.get();
        var role = authentication.getAuthorities().stream().map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_")).map(a -> a.substring(5)).findFirst().orElse(null);
        if (role == null) return CURRENT_USER.get();
        return new UserPrincipal(authentication.getName(), null, za.ac.cput.domain.RoleEnum.valueOf(role));
    }

    public static void setCurrentUser(UserPrincipal principal) {
        CURRENT_USER.set(principal);
    }

    public static void clearCurrentUser() {
        CURRENT_USER.remove();
    }
}
