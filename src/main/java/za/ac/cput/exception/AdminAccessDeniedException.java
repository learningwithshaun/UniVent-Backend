package za.ac.cput.exception;

import org.springframework.http.HttpStatus;

public class AdminAccessDeniedException extends ApiException {

    public AdminAccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, "Forbidden", message);
    }
}
