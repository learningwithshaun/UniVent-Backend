package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Administrator;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.User;
import za.ac.cput.dtos.EventResponseDTO;
import za.ac.cput.dtos.PlatformStatsDTO;
import za.ac.cput.dtos.UserResponseDTO;
import za.ac.cput.service.AdministratorService;

import java.util.List;
import java.util.stream.Collectors;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorController.java
 * Date: 05 July 2026
 **/

@RestController
@RequestMapping("/api/admin")
public class AdministratorController {
    private final AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }


    @GetMapping("/events")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventResponseDTO>> monitorEvents() {
        return ResponseEntity.ok(
                administratorService.monitorEvents().stream()
                        .map(this::toEventDTO)
                        .collect(Collectors.toList()));
    }


    @GetMapping("/events/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventResponseDTO>> getPendingEvents() {
        return ResponseEntity.ok(
                administratorService.viewPendingEvents().stream()
                        .map(this::toEventDTO)
                        .collect(Collectors.toList()));
    }


    @PatchMapping("/events/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> approveEvent(@PathVariable Integer id) {
        Event event = administratorService.approveEvent(id);
        return event == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(toEventDTO(event));
    }


    @PatchMapping("/events/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> rejectEvent(@PathVariable Integer id) {
        Event event = administratorService.rejectEvent(id);
        return event == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(toEventDTO(event));
    }


    @PatchMapping("/events/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> disableEvent(@PathVariable Integer id) {
        Event event = administratorService.disableEvent(id);
        return event == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(toEventDTO(event));
    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> viewAllUsers() {
        return ResponseEntity.ok(
                administratorService.viewAllUsers().stream()
                        .map(this::toUserDTO)
                        .collect(Collectors.toList()));
    }


    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        administratorService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> disableUser(@PathVariable String id) {
        User user = administratorService.disableUser(id);
        return user == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(toUserDTO(user));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlatformStatsDTO> getPlatformStats() {
        return ResponseEntity.ok(administratorService.getPlatformStats());
    }

    // ---- Administrator CRUD ----
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Administrator> createAdministrator(@RequestBody Administrator administrator) {
        return ResponseEntity.ok(administratorService.create(administrator));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Administrator> getAdministratorById(@PathVariable String id) {
        Administrator administrator = administratorService.read(id);
        return administrator == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(administrator);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Administrator> updateAdministrator(@RequestBody Administrator administrator) {
        return ResponseEntity.ok(administratorService.update(administrator));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        administratorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private EventResponseDTO toEventDTO(Event event) {
        return new EventResponseDTO(
                event.getEventId(),
                event.getName(),
                event.getDescription(),
                event.getDateTime(),
                event.getMaxAttendees(),
                event.getStatus()
        );
    }

    private UserResponseDTO toUserDTO(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.isDisabled()
        );
    }
}