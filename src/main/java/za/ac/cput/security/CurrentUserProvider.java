package za.ac.cput.security;

/**
 * Contract for resolving the currently authenticated user of a request.
 * Implementation is intentionally a thread-local stub here; BE 01 (authentication)
 * is expected to provide a JWT-backed implementation of this same interface.
 */
public interface CurrentUserProvider {

    /**
     * @return the principal of the current request, or null when no valid token is present
     */
    UserPrincipal getCurrentUser();
}
