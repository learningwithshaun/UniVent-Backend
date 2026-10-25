package za.ac.cput.dto;
public record AdminEventSummaryDTO(String id, String name, String organizerName, String dateTime,
                                   String status, long registrationCount) {}
