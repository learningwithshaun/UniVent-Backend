package za.ac.cput.dto;
import za.ac.cput.domain.EventStatus;
import java.time.LocalDateTime;
public record OrganizerEventSummaryDTO(String id, String name, LocalDateTime dateTime, String venueName,
                                      EventStatus status, long registrationCount) {}
