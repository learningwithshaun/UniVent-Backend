package za.ac.cput.dto;

/**
 * Summary view of a user for administrator screens.
 * Never contains the password hash or any token-related field.
 */
public record UserResponseDTO(
        String id,
        String name,
        String email,
        String role,                 // STUDENT / ORGANIZER / ADMIN
        String facultyOrOrganization,
        String joinDate,             // ISO 8601
        String accountStatus         // ACTIVE / DISABLED
) {
}
