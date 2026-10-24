package za.ac.cput.dto;

import java.util.List;

/**
 * Platform-wide statistics for the administrator dashboard.
 * Recent lists are limited to 5 items each, sorted most recent first.
 */
public record PlatformStatsDTO(
        long totalEvents,
        long totalUsers,
        long totalBookings,
        List<AdminEventSummaryDTO> recentEvents,
        List<BookingSummaryDTO> recentRegistrations
) {
}
