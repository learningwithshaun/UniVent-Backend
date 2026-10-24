package za.ac.cput.exception;

import org.springframework.http.HttpStatus;

public class SelfDisableException extends ApiException {

    public SelfDisableException() {
        super(HttpStatus.BAD_REQUEST, "Bad Request",
                "Cannot disable your own administrator account");
    }
}
