package za.ac.cput.dto;
public record UserResponseDTO(String id, String name, String email, String role,
                              String accountStatus, String createdAt) {}
