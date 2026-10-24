package za.ac.cput.dto;

import java.util.List;

/**
 * Summary view of an event for administrator screens. No JPA entity or nested graph is exposed.
 */
public record AdminEventSummaryDTO(
        String id,
        String name,
        String organizerName,
        String dateTime,       // ISO 8601
        String category,
        String status,         // stable string enum value, e.g. PENDING_APPROVAL / APPROVED / DISABLED
        int registrationCount,
        List<String> allowedActions
) {
}
