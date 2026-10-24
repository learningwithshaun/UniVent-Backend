package za.ac.cput.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {

    public UserNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, "Not Found", "User with id " + id + " does not exist");
    }
}
