package za.ac.cput.dto;
public record ApiErrorDTO(String timestamp, int status, String error, String message, String path) {}
