package za.ac.cput.exception;

import org.springframework.http.HttpStatus;

public class NotAuthenticatedException extends ApiException {

    public NotAuthenticatedException(String message) {
        super(HttpStatus.UNAUTHORIZED, "Unauthorized", message);
    }
}
