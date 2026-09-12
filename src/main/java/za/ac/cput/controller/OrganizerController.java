package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.Organizer;
import za.ac.cput.dtos.EventResponseDTO;
import za.ac.cput.service.OrganizerService;

import java.util.List;
import java.util.stream.Collectors;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * OrganizerController.java
 * Date: 05 July 2026
 **/

@RestController
@RequestMapping("/api/organizers")
public class OrganizerController {
    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }


    @PostMapping("/{organizerId}/events")
    public ResponseEntity<EventResponseDTO> createEvent(@PathVariable String organizerId,
                                                        @RequestBody Event event) {
        Organizer organizer = organizerService.read(organizerId);
        if (organizer == null) {
            return ResponseEntity.notFound().build();
        }
        event.setOrganizer(organizer);
        return ResponseEntity.ok(toEventDTO(organizerService.createEvent(event)));
    }


    @PutMapping("/events")
    public ResponseEntity<EventResponseDTO> updateEvent(@RequestBody Event event) {
        Event updated = organizerService.updateEvent(event);
        return updated == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(toEventDTO(updated));
    }


    @PatchMapping("/{organizerId}/events/{eventId}/cancel")
    public ResponseEntity<EventResponseDTO> cancelEvent(@PathVariable String organizerId,
                                                        @PathVariable Integer eventId) {
        Event event = organizerService.cancelEvent(eventId, organizerId);
        return event == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(toEventDTO(event));
    }


    @GetMapping("/{organizerId}/events")
    public ResponseEntity<List<EventResponseDTO>> viewMyEvents(@PathVariable String organizerId) {
        return ResponseEntity.ok(
                organizerService.viewMyEvents(organizerId).stream()
                        .map(this::toEventDTO)
                        .collect(Collectors.toList()));
    }


    @GetMapping("/{organizerId}/events/{eventId}/registrations/count")
    public ResponseEntity<Integer> getTotalRegistrations(@PathVariable String organizerId,
                                                         @PathVariable Integer eventId) {
        return ResponseEntity.ok(organizerService.getTotalRegistrations(eventId, organizerId));
    }

    // ---- Organizer CRUD ----
    @PostMapping
    public ResponseEntity<Organizer> createOrganizer(@RequestBody Organizer organizer) {
        return ResponseEntity.ok(organizerService.create(organizer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organizer> getOrganizerById(@PathVariable String id) {
        Organizer organizer = organizerService.read(id);
        return organizer == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(organizer);
    }

    @PutMapping
    public ResponseEntity<Organizer> updateOrganizer(@RequestBody Organizer organizer) {
        return ResponseEntity.ok(organizerService.update(organizer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        organizerService.delete(id);
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
}