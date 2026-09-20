package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 *Description: Ticket
 *Domain class - Spring Boot / JPA version
 **/

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    private String ticketId;
    private int bookingId;
    private LocalDate issueDate;
    private String ticketCode;
    private boolean used;

    protected Ticket() {
        /* required by JPA */
    }

    private Ticket(Builder builder) {
        this.ticketId = builder.ticketId;
        this.bookingId = builder.bookingId;
        this.issueDate = builder.issueDate;
        this.ticketCode = builder.ticketCode;
        this.used = builder.used;
    }

    public String getTicketId() {
        return ticketId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public boolean isUsed() {
        return used;
    }

    // Business method, following the same pattern as Booking.cancel()/confirm()
    public void markAsUsed() {
        this.used = true;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", bookingId=" + bookingId +
                ", issueDate=" + issueDate +
                ", ticketCode='" + ticketCode + '\'' +
                ", used=" + used +
                '}';
    }

    public static class Builder {
        private String ticketId;
        private int bookingId;
        private LocalDate issueDate;
        private String ticketCode;
        private boolean used;

        public Builder setTicketId(String ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder setBookingId(int bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder setIssueDate(LocalDate issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public Builder setTicketCode(String ticketCode) {
            this.ticketCode = ticketCode;
            return this;
        }

        public Builder setUsed(boolean used) {
            this.used = used;
            return this;
        }

        public Ticket build() {
            return new Ticket(this);
        }
    }
}