package za.ac.cput.controller;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.dto.*;
import za.ac.cput.security.CurrentUserProvider;
import za.ac.cput.service.IAdministratorService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdministratorController {
    private final IAdministratorService service;
    private final CurrentUserProvider current;

    public AdministratorController(IAdministratorService service, CurrentUserProvider current) {
        this.service = service;
        this.current = current;
    }

    @GetMapping("/stats")
    public PlatformStatsDTO stats() {
        return service.getStats();
    }

    @GetMapping("/events")
    public List<AdminEventSummaryDTO> events(@RequestParam(defaultValue = "ALL") String status) {
        return service.getEvents(status);
    }

    @PatchMapping("/events/{id}/approve")
    public AdminEventSummaryDTO approve(@PathVariable String id) {
        return service.approveEvent(id);
    }

    @PatchMapping("/events/{id}/disable")
    public AdminEventSummaryDTO disable(@PathVariable String id) {
        return service.disableEvent(id);
    }

    @GetMapping("/users")
    public Page<UserResponseDTO> users(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid page or size");
        }
        return service.getUsers(search, role, PageRequest.of(page, size));
    }

    @PatchMapping("/users/{id}/disable")
    public UserResponseDTO disableUser(@PathVariable String id) {
        var principal = current.getCurrentUser();
        return service.disableUser(id, principal == null ? null : principal.userId());
    }
}
