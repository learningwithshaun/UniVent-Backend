package za.ac.cput.dto;

/**
 * Shared error body returned by admin endpoints.
 * Fields: timestamp, status, error, message, path.
 */
public record ErrorDTO(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
