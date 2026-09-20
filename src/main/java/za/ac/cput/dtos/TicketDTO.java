package za.ac.cput.dtos;

import java.time.LocalDate;

public class TicketDTO {

    private String ticketId;
    private String ticketCode;
    private LocalDate issueDate;
    private boolean used;
    private String eventName;
    private String eventDateTime;
    private String venue;
    private String studentName;

    public TicketDTO() {
    }

    public TicketDTO(String ticketId, String ticketCode, LocalDate issueDate, boolean used,
                     String eventName, String eventDateTime, String venue, String studentName) {
        this.ticketId = ticketId;
        this.ticketCode = ticketCode;
        this.issueDate = issueDate;
        this.used = used;
        this.eventName = eventName;
        this.eventDateTime = eventDateTime;
        this.venue = venue;
        this.studentName = studentName;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public boolean isUsed() {
        return used;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public String getVenue() {
        return venue;
    }

    public String getStudentName() {
        return studentName;
    }
}