package za.ac.cput.dto;

/**
 * Summary view of a booking (registration), used for recent activity in platform stats.
 */
public record BookingSummaryDTO(
        String id,
        String eventId,
        String eventName,
        String userId,
        String userName,
        String bookedAt,   // ISO 8601
        String status
) {
}
