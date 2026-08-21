package za.ac.cput.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.dtos.AuthResponse;
import za.ac.cput.dtos.LoginRequest;
import za.ac.cput.dtos.RegisterRequest;
import za.ac.cput.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    /**
     * Example request body for testing:
     * {
     *   "name": "Samukelo Dlamini",
     *   "email": "221234567@mycput.ac.za",
     *   "password": "securepass123",
     *   "phoneNumber": "0821234567",
     *   "role": "STUDENT",
     *   "studentNumber": "221234567",
     *   "faculty": "Informatics and Design",
     *   "department": "Information Technology",
     *   "yearOfStudy": 3
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    public static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
