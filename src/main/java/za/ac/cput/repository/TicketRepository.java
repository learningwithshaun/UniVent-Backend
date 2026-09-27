package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Ticket;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    Optional<Ticket> findByTicketCode(String ticketCode);
    Optional<Ticket> findByBookingId(int bookingId);
}