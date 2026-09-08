package za.ac.cput.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Ticket;
import za.ac.cput.dtos.TicketDTO;
import za.ac.cput.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;
    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket createdTicket = ticketService.create(ticket);
        return ResponseEntity.ok(createdTicket);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.read(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }
    @PutMapping()
    public ResponseEntity<Ticket> updateTicket(@RequestBody Ticket ticket) {
        Ticket updatedTicket = ticketService.update(ticket);
        return ResponseEntity.ok(updatedTicket);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Issue #23 ---

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<TicketDTO> getTicketByBooking(
            @PathVariable int bookingId,
            @RequestParam String requestingUserId) {
        // NOTE: requestingUserId is a request param for now, since there's
        // no security/auth layer wired up yet to pull it from a JWT/session.
        TicketDTO ticket = ticketService.getTicketByBookingId(bookingId, requestingUserId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/code/{ticketCode}")
    public ResponseEntity<Ticket> getTicketByCode(@PathVariable String ticketCode) {
        // NOTE: issue #23 restricts this to ORGANIZER or ADMIN roles.
        // Role-based access isn't enforced yet in this project.
        Ticket ticket = ticketService.getTicketByCode(ticketCode);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/code/{ticketCode}/use")
    public ResponseEntity<Ticket> markTicketUsed(@PathVariable String ticketCode) {
        // NOTE: same ORGANIZER/ADMIN restriction as above, not yet enforced.
        Ticket updated = ticketService.markTicketUsed(ticketCode);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}
