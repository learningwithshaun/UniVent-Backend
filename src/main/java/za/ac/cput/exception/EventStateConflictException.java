package za.ac.cput.exception;

import org.springframework.http.HttpStatus;

public class EventStateConflictException extends ApiException {

    public EventStateConflictException(String message) {
        super(HttpStatus.CONFLICT, "Conflict", message);
    }
}
