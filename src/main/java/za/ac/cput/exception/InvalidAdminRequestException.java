package za.ac.cput.exception;

import org.springframework.http.HttpStatus;

public class InvalidAdminRequestException extends ApiException {

    public InvalidAdminRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, "Bad Request", message);
    }
}
