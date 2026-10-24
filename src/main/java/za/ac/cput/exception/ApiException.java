package za.ac.cput.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for admin API errors. Carries the HTTP status to be rendered
 * in the shared {@link za.ac.cput.dto.ErrorDTO} body.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String error;

    public ApiException(HttpStatus status, String error, String message) {
        super(message);
        this.status = status;
        this.error = error;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
