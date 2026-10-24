package za.ac.cput.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.dto.AdminEventSummaryDTO;
import za.ac.cput.dto.ErrorDTO;
import za.ac.cput.dto.PlatformStatsDTO;
import za.ac.cput.dto.UserResponseDTO;
import za.ac.cput.exception.ApiException;
import za.ac.cput.exception.AdminAccessDeniedException;
import za.ac.cput.exception.EventNotFoundException;
import za.ac.cput.exception.EventStateConflictException;
import za.ac.cput.exception.InvalidAdminRequestException;
import za.ac.cput.exception.NotAuthenticatedException;
import za.ac.cput.exception.SelfDisableException;
import za.ac.cput.exception.UserNotFoundException;
import za.ac.cput.security.CurrentUserProvider;
import za.ac.cput.service.IAdminService;

import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

/**
 * Administrator API contract (issue BE 05).
 * <p>
 * All routes are under /api/admin and require the ADMIN role. Authentication is resolved
 * through {@link CurrentUserProvider} (populated by the JWT layer owned by BE 01; a thread-local
 * stub is installed until then). No JPA entity is returned from any endpoint.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final IAdminService adminService;
    private final CurrentUserProvider currentUserProvider;

    public AdminController(IAdminService adminService, CurrentUserProvider currentUserProvider) {
        this.adminService = adminService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/stats")
    public ResponseEntity<PlatformStatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/events")
    public ResponseEntity<List<AdminEventSummaryDTO>> getEvents(
            @RequestParam(defaultValue = "ALL") String status) {
        return ResponseEntity.ok(adminService.getEvents(status));
    }

    @PatchMapping("/events/{id}/approve")
    public ResponseEntity<AdminEventSummaryDTO> approveEvent(@PathVariable String id) {
        return ResponseEntity.ok(adminService.approveEvent(id));
    }

    @PatchMapping("/events/{id}/disable")
    public ResponseEntity<AdminEventSummaryDTO> disableEvent(@PathVariable String id) {
        return ResponseEntity.ok(adminService.disableEvent(id));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDTO>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getUsers(search, role, pageable));
    }

    @PatchMapping("/users/{id}/disable")
    public ResponseEntity<UserResponseDTO> disableUser(@PathVariable String id) {
        String currentUserId = currentUserFromToken();
        return ResponseEntity.ok(adminService.disableUser(id, currentUserId));
    }

    /* ---------------------------------------------------------------- errors */

    private String currentUserFromToken() {
        if (currentUserProvider.getCurrentUser() == null) {
            throw new NotAuthenticatedException("No valid authentication token present");
        }
        return currentUserProvider.getCurrentUser().userId();
    }

    @ExceptionHandler(NotAuthenticatedException.class)
    public ResponseEntity<ErrorDTO> handleNotAuthenticated(NotAuthenticatedException ex, HttpServletRequest request) {
        return buildError(ex, UNAUTHORIZED, request);
    }

    @ExceptionHandler(AdminAccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleAccessDenied(AdminAccessDeniedException ex, HttpServletRequest request) {
        return buildError(ex, FORBIDDEN, request);
    }

    @ExceptionHandler({EventNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorDTO> handleNotFound(ApiException ex, HttpServletRequest request) {
        return buildError(ex, NOT_FOUND, request);
    }

    @ExceptionHandler(EventStateConflictException.class)
    public ResponseEntity<ErrorDTO> handleConflict(EventStateConflictException ex, HttpServletRequest request) {
        return buildError(ex, CONFLICT, request);
    }

    @ExceptionHandler({SelfDisableException.class, InvalidAdminRequestException.class})
    public ResponseEntity<ErrorDTO> handleBadRequest(ApiException ex, HttpServletRequest request) {
        return buildError(ex, BAD_REQUEST, request);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorDTO> handleApiException(ApiException ex, HttpServletRequest request) {
        return buildError(ex, ex.getStatus(), request);
    }

    private ResponseEntity<ErrorDTO> buildError(ApiException ex, HttpStatus status, HttpServletRequest request) {
        ErrorDTO error = new ErrorDTO(
                Instant.now().toString(),
                status.value(),
                ex.getError(),
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
