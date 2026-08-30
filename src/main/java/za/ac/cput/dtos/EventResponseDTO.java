package za.ac.cput.dtos;

import za.ac.cput.domain.EventStatusEnum;

/**
 * DTO for event response data
 */
public class EventResponseDTO {
    private int eventId;
    private String name;
    private String description;
    private String dateTime;
    private int maxAttendees;
    private EventStatusEnum status;

    public EventResponseDTO(int eventId, String name, String description, String dateTime, int maxAttendees, EventStatusEnum status) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.maxAttendees = maxAttendees;
        this.status = status;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public EventStatusEnum getStatus() {
        return status;
    }

    public void setStatus(EventStatusEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EventResponseDTO{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", maxAttendees=" + maxAttendees +
                ", status=" + status +
                '}';
    }
}
