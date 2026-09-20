package za.ac.cput.univentbackend.factoryTest;
/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 * Date:  July 2026
 **/
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Ticket;
import za.ac.cput.factory.TicketFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TicketFactoryTest {

    @Test
    public void shouldCreateValidTicket() {

        Ticket ticket = TicketFactory.createTicket(
                1,
                LocalDate.of(2026, 8, 15),
                "1001"
        );

        assertNotNull(ticket);
        assertEquals(1, ticket.getBookingId());
        assertEquals(LocalDate.of(2026, 8, 15), ticket.getIssueDate());
        assertEquals("1001", ticket.getTicketCode());
    }

    @Test
    public void shouldThrowIfBookingIdIsZero() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        0,
                        LocalDate.of(2026, 8, 15),
                        "1001"));

        assertEquals("Booking ID is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfBookingIdIsNegative() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        -1,
                        LocalDate.of(2026, 8, 15),
                        "1001"));

        assertEquals("Booking ID is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfIssueDateIsNull() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        1,
                        null,
                        "1001"));

        assertEquals("Issue date is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfTicketCodeIsEmpty() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        1,
                        LocalDate.of(2026, 8, 15),
                        ""));

        assertEquals("Ticket code is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfTicketCodeIsNull() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        1,
                        LocalDate.of(2026, 8, 15),
                        null));

        assertEquals("Ticket code is required", exception.getMessage());
    }
}