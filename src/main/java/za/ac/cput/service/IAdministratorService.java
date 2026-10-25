package za.ac.cput.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import za.ac.cput.dto.AdminEventSummaryDTO;
import za.ac.cput.dto.PlatformStatsDTO;
import za.ac.cput.dto.UserResponseDTO;
import java.util.List;

public interface IAdministratorService {

    PlatformStatsDTO getStats();

    List<AdminEventSummaryDTO> getEvents(String status);

    AdminEventSummaryDTO approveEvent(String id);

    AdminEventSummaryDTO disableEvent(String id);

    Page<UserResponseDTO> getUsers(String search, String role, Pageable pageable);

    UserResponseDTO disableUser(String id, String currentUserId);
}
