package za.ac.cput.domain;

import jakarta.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;
    private int bookingId;
    private int issueDate;
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

    public Long getTicketId() {
        return ticketId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getIssueDate() {
        return issueDate;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public boolean isUsed() {
        return used;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", bookingId=" + bookingId +
                ", issueDate=" + issueDate +
                ", ticketCode='" + ticketCode + '\'' +
                ", used=" + used +
                '}';
    }

    public static class Builder {
        private Long ticketId;
        private int bookingId;
        private int issueDate;
        private String ticketCode;
        private boolean used;

        public Builder setTicketId(Long ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder setBookingId(int bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder setIssueDate(int issueDate) {
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