package za.ac.cput.service;
/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 *title: Service Milestone
 * Date: 12 July 2026
 **/

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.Ticket;
import za.ac.cput.dtos.TicketDTO;
import za.ac.cput.exception.UnauthorizedException;
import za.ac.cput.factory.TicketFactory;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.TicketRepository;
import za.ac.cput.util.Helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class TicketService implements ITicketService {

    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;

    public TicketService(TicketRepository ticketRepository, BookingRepository bookingRepository) {
        this.ticketRepository = ticketRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Ticket findByBookingId(int bookingId) {
        return ticketRepository.findByBookingId(bookingId).orElse(null);
    }

    @Override
    public Ticket create(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket read(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @Override
    public Ticket update(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void delete(Long id) {
        ticketRepository.deleteById(id);
    }

    // --- Issue #22 ---

    @Override
    public TicketDTO generateTicket(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking is required");
        }

        String ticketCode = Helper.generateId();
        int issueDate = Integer.parseInt(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        Ticket ticket = TicketFactory.createTicket(booking.getBookingId(), issueDate, ticketCode);
        Ticket saved = ticketRepository.save(ticket);

        return toDTO(saved, booking);
    }

    @Override
    public TicketDTO getTicketByBookingId(int bookingId, String requestingUserId) {
        Ticket ticket = ticketRepository.findByBookingId(bookingId).orElse(null);
        if (ticket == null) {
            return null;
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return null;
        }

        if (booking.getStudent() == null
                || !booking.getStudent().getUserId().equals(requestingUserId)) {
            throw new UnauthorizedException("You do not own this booking");
        }

        return toDTO(ticket, booking);
    }

    @Override
    public Ticket getTicketByCode(String ticketCode) {
        return ticketRepository.findByTicketCode(ticketCode).orElse(null);
    }

    @Override
    public Ticket markTicketUsed(String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode).orElse(null);
        if (ticket == null) {
            return null;
        }

        Ticket updated = new Ticket.Builder()
                .setTicketId(ticket.getTicketId())
                .setBookingId(ticket.getBookingId())
                .setIssueDate(ticket.getIssueDate())
                .setTicketCode(ticket.getTicketCode())
                .setUsed(true)
                .build();

        return ticketRepository.save(updated);
    }

    private TicketDTO toDTO(Ticket ticket, Booking booking) {
        Event event = booking.getEvent();
        String eventName = event != null ? event.getName() : null;
        String eventDateTime = event != null ? event.getDateTime() : null;
        String venue = (event != null && event.getVenue() != null)
                ? event.getVenue().getVenueName() : null;
        String studentName = booking.getStudent() != null ? booking.getStudent().getName() : null;

        return new TicketDTO(
                ticket.getTicketId(),
                ticket.getTicketCode(),
                ticket.getIssueDate(),
                ticket.isUsed(),
                eventName,
                eventDateTime,
                venue,
                studentName
        );
    }
}