package za.ac.cput.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.EventStatus;
import za.ac.cput.dto.OrganizerEventSummaryDTO;
import za.ac.cput.service.OrganizerEventService;
import java.util.List;

@RestController
@RequestMapping("/api/organizers/me/events")
public class OrganizerEventController {
    private final OrganizerEventService service;
    public OrganizerEventController(OrganizerEventService service) { this.service=service; }
    @GetMapping public List<OrganizerEventSummaryDTO> list(@RequestParam(required=false) String status, Authentication auth) {
        EventStatus parsed=null;
        if(status!=null && !status.isBlank()) {
            try { parsed=EventStatus.valueOf(status.trim().toUpperCase(java.util.Locale.ROOT)); }
            catch (RuntimeException ex) { throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Unknown event status"); }
        }
        String organizerId=auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"))?null:auth.getName();
        return service.list(organizerId,parsed);
    }
}
