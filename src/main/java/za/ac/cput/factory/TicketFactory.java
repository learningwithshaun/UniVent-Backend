package za.ac.cput.factory;
import za.ac.cput.domain.Ticket;
import za.ac.cput.util.Helper;

import java.time.LocalDate;

public class TicketFactory {
    public static Ticket createTicket(int bookingId,
                                      LocalDate issueDate,
                                      String ticketCode) {
        if (!Helper.isPositive(bookingId)) {
            throw new IllegalArgumentException("Booking ID is required");
        }
        if (issueDate == null) {
            throw new IllegalArgumentException("Issue date is required");
        }
        if (Helper.isNullOrEmpty(ticketCode)) {
            throw new IllegalArgumentException("Ticket code is required");
        }
        return new Ticket.Builder()
                .setTicketId(Helper.generateId())
                .setBookingId(bookingId)
                .setIssueDate(issueDate)
                .setTicketCode(ticketCode)
                .setUsed(false)
                .build();
    }
}