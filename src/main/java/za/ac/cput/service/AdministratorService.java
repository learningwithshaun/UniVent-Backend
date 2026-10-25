package za.ac.cput.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.AccountStatus;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatus;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.User;
import za.ac.cput.dto.AdminEventSummaryDTO;
import za.ac.cput.dto.PlatformStatsDTO;
import za.ac.cput.dto.UserResponseDTO;
import za.ac.cput.exception.EventNotFoundException;
import za.ac.cput.exception.EventStateConflictException;
import za.ac.cput.exception.InvalidAdminRequestException;
import za.ac.cput.exception.SelfDisableException;
import za.ac.cput.exception.UserNotFoundException;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.UserRepository;

import java.util.List;
import java.util.Locale;

@Service
public class AdministratorService implements IAdministratorService {
    private final EventRepository events;
    private final BookingRepository bookings;
    private final UserRepository users;

    public AdministratorService(EventRepository events, BookingRepository bookings, UserRepository users) {
        this.events = events;
        this.bookings = bookings;
        this.users = users;
    }

    @Override
    public PlatformStatsDTO getStats() {
        return new PlatformStatsDTO(events.countAllEvents(), users.countAllUsers(), bookings.countAllBookings());
    }

    @Override
    public List<AdminEventSummaryDTO> getEvents(String rawStatus) {
        EventStatus status = null;
        if (rawStatus != null && !rawStatus.isBlank() && !rawStatus.equalsIgnoreCase("ALL")) {
            try {
                status = EventStatus.valueOf(rawStatus.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw new InvalidAdminRequestException("Unknown status: " + rawStatus);
            }
        }
        return (status == null ? events.findAll() : events.findByStatus(status))
                .stream()
                .map(this::toEventSummary)
                .toList();
    }

    @Override
    public AdminEventSummaryDTO approveEvent(String id) {
        Event event = events.findById(id).orElseThrow(() -> new EventNotFoundException(id));
        if (event.getStatus() != EventStatus.PENDING_APPROVAL) {
            throw new EventStateConflictException("Event is not pending approval");
        }
        event.setStatus(EventStatus.APPROVED);
        return toEventSummary(events.save(event));
    }

    @Override
    public AdminEventSummaryDTO disableEvent(String id) {
        Event event = events.findById(id).orElseThrow(() -> new EventNotFoundException(id));
        if (event.getStatus() == EventStatus.DISABLED) {
            throw new EventStateConflictException("Event is already disabled");
        }
        event.setStatus(EventStatus.DISABLED);
        return toEventSummary(events.save(event));
    }

    @Override
    public Page<UserResponseDTO> getUsers(String search, String role, Pageable pageable) {
        RoleEnum parsedRole = null;
        if (role != null && !role.isBlank()) {
            try {
                parsedRole = RoleEnum.valueOf(role.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw new InvalidAdminRequestException("Unknown role: " + role);
            }
        }
        return users.findByRoleAndSearch(parsedRole, search, pageable).map(this::toUserResponse);
    }

    @Override
    public UserResponseDTO disableUser(String id, String currentUserId) {
        if (id.equals(currentUserId)) {
            throw new SelfDisableException();
        }
        User user = users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setAccountStatus(AccountStatus.DISABLED);
        return toUserResponse(users.save(user));
    }

    private AdminEventSummaryDTO toEventSummary(Event event) {
        return new AdminEventSummaryDTO(
                event.getId(),
                event.getName(),
                event.getOrganizer() == null ? null : event.getOrganizer().getName(),
                event.getDateTime() == null ? null : event.getDateTime().toString(),
                event.getStatus() == null ? null : event.getStatus().name(),
                bookings.countByEvent_Id(event.getId()));
    }

    private UserResponseDTO toUserResponse(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole() == null ? null : user.getRole().name(),
                user.getAccountStatus() == null ? null : user.getAccountStatus().name(),
                user.getCreatedAt() == null ? null : user.getCreatedAt().toString());
    }
}
