package za.ac.cput.dtos;

public class TicketDTO {

    private Long ticketId;
    private String ticketCode;
    private int issueDate;
    private boolean used;
    private String eventName;
    private String eventDateTime;
    private String venue;
    private String studentName;

    public TicketDTO() {
    }

    public TicketDTO(Long ticketId, String ticketCode, int issueDate, boolean used,
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

    public Long getTicketId() {
        return ticketId;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public int getIssueDate() {
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