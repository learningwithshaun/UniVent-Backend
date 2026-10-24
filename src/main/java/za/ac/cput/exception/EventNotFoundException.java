package za.ac.cput.exception;

import org.springframework.http.HttpStatus;

public class EventNotFoundException extends ApiException {

    public EventNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, "Not Found", "Event with id " + id + " does not exist");
    }
}
