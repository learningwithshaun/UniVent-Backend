package za.ac.cput.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.dto.ApiErrorDTO;
import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(za.ac.cput.exception.ApiException.class)
    public ResponseEntity<ApiErrorDTO> api(za.ac.cput.exception.ApiException ex, HttpServletRequest request) {
        HttpStatus status=ex.getStatus();
        return ResponseEntity.status(status).body(new ApiErrorDTO(Instant.now().toString(),status.value(),ex.getError(),ex.getMessage(),request.getRequestURI()));
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDTO> responseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status=HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status).body(new ApiErrorDTO(Instant.now().toString(),status.value(),status.getReasonPhrase(),ex.getReason(),request.getRequestURI()));
    }
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorDTO> badRequest(Exception ex, HttpServletRequest request) {
        HttpStatus status=HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ApiErrorDTO(Instant.now().toString(),400,status.getReasonPhrase(),"Invalid request parameter or value",request.getRequestURI()));
    }
}
