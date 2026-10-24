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
        return CURRENT_USER.get();
    }

    public static void setCurrentUser(UserPrincipal principal) {
        CURRENT_USER.set(principal);
    }

    public static void clearCurrentUser() {
        CURRENT_USER.remove();
    }
}
