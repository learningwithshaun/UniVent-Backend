package za.ac.cput.service;
/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 *title: Service Milestone
 * Date: 12 July 2026
 **/

import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Ticket;
import za.ac.cput.dtos.TicketDTO;

public interface ITicketService extends IService<Ticket, String> {
    Ticket findByBookingId(int bookingId);

    // Issue #22
    TicketDTO generateTicket(Booking booking);
    TicketDTO getTicketByBookingId(int bookingId, String requestingUserId);
    Ticket getTicketByCode(String ticketCode);
    Ticket markTicketUsed(String ticketCode);
}