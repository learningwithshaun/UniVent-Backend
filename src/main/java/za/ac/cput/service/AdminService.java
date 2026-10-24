package za.ac.cput.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.BookingStatusEnum;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.User;
import za.ac.cput.dto.AdminEventSummaryDTO;
import za.ac.cput.dto.BookingSummaryDTO;
import za.ac.cput.dto.PlatformStatsDTO;
import za.ac.cput.dto.UserResponseDTO;
import za.ac.cput.exception.AdminAccessDeniedException;
import za.ac.cput.exception.EventNotFoundException;
import za.ac.cput.exception.EventStateConflictException;
import za.ac.cput.exception.InvalidAdminRequestException;
import za.ac.cput.exception.NotAuthenticatedException;
import za.ac.cput.exception.SelfDisableException;
import za.ac.cput.exception.UserNotFoundException;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.security.CurrentUserProvider;
import za.ac.cput.security.UserPrincipal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {

    private static final int RECENT_LIMIT = 5;

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public AdminService(EventRepository eventRepository,
                        BookingRepository bookingRepository,
                        UserRepository userRepository,
                        CurrentUserProvider currentUserProvider) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
    }

    /* ---------------------------------------------------------------- auth */

    private UserPrincipal requireAdmin() {
        UserPrincipal current = currentUserProvider.getCurrentUser();
        if (current == null) {
            throw new NotAuthenticatedException("No valid authentication token present");
        }
        if (current.role() != RoleEnum.ADMIN) {
            throw new AdminAccessDeniedException("ADMIN role required");
        }
        return current;
    }

    /* ---------------------------------------------------------------- stats */

    @Override
    public PlatformStatsDTO getStats() {
        requireAdmin();
        long totalEvents = eventRepository.count();
        long totalUsers = userRepository.count();
        long totalBookings = bookingRepository.count();

        List<AdminEventSummaryDTO> recentEvents = eventRepository.findTop5ByOrderByDateTimeDesc().stream()
                .map(this::toEventSummary).collect(Collectors.toList());

        List<Booking> recentBookings = bookingRepository.findTop5ByOrderByBookedAtDesc();
        List<BookingSummaryDTO> recentRegistrations = recentBookings.stream()
                .map(this::toBookingSummary).collect(Collectors.toList());

        return new PlatformStatsDTO(totalEvents, totalUsers, totalBookings, recentEvents, recentRegistrations);
    }

    /* ---------------------------------------------------------------- events */

    @Override
    public List<AdminEventSummaryDTO> getEvents(String status) {
        requireAdmin();
        EventStatusEnum filter = parseEventStatus(status);
        List<Event> events = (filter == null)
                ? eventRepository.findAll()
                : eventRepository.findByStatus(filter, org.springframework.data.domain.Pageable.unpaged());
        return events.stream().map(this::toEventSummary).collect(Collectors.toList());
    }

    @Override
    public AdminEventSummaryDTO approveEvent(String id) {
        requireAdmin();
        Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
        if (event.getStatus() != EventStatusEnum.PENDING_APPROVAL) {
            throw new EventStateConflictException(
                    "Event " + id + " is not pending approval (current status: " + event.getStatus() + ")");
        }
        event.setStatus(EventStatusEnum.APPROVED);
        eventRepository.save(event);
        return toEventSummary(event);
    }

    @Override
    public AdminEventSummaryDTO disableEvent(String id) {
        requireAdmin();
        Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
        if (event.getStatus() == EventStatusEnum.DISABLED) {
            throw new EventStateConflictException("Event " + id + " is already disabled");
        }
        event.setStatus(EventStatusEnum.DISABLED);
        eventRepository.save(event);
        return toEventSummary(event);
    }

    /* ---------------------------------------------------------------- users */

    @Override
    public Page<UserResponseDTO> getUsers(String search, String role, Pageable pageable) {
        requireAdmin();
        RoleEnum roleFilter = parseRole(role);
        Page<User> users = userRepository.findByRoleAndSearch(roleFilter, search, pageable);
        return users.map(this::toUserResponse);
    }

    @Override
    public UserResponseDTO disableUser(String id, String currentUserId) {
        requireAdmin();
        if (id.equals(currentUserId)) {
            throw new SelfDisableException();
        }
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setEnabled(false);
        userRepository.save(user);
        return toUserResponse(user);
    }

    /* ---------------------------------------------------------------- helpers */

    private EventStatusEnum parseEventStatus(String status) {
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return null;
        }
        try {
            return EventStatusEnum.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidAdminRequestException(
                    "Unknown status '" + status + "'. Allowed values: ALL, PENDING_APPROVAL, APPROVED, DISABLED");
        }
    }

    private RoleEnum parseRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        try {
            return RoleEnum.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidAdminRequestException(
                    "Unknown role '" + role + "'. Allowed values: STUDENT, ORGANIZER, ADMIN");
        }
    }

    private AdminEventSummaryDTO toEventSummary(Event event) {
        List<String> actions = switch (event.getStatus()) {
            case PENDING_APPROVAL -> List.of("APPROVE", "DISABLE");
            case DISABLED -> List.of();
            default -> List.of("DISABLE");
        };
        String organizerName = event.getOrganizer() != null ? event.getOrganizer().getName() : null;
        return new AdminEventSummaryDTO(
                event.getEventId(),
                event.getName(),
                organizerName,
                event.getDateTime() != null ? event.getDateTime().toString() : null,
                event.getCategory(),
                event.getStatus() != null ? event.getStatus().name() : null,
                event.getRegistrationCount(),
                actions);
    }

    private BookingSummaryDTO toBookingSummary(Booking booking) {
        return new BookingSummaryDTO(
                booking.getBookingId(),
                booking.getEvent() != null ? booking.getEvent().getEventId() : null,
                booking.getEvent() != null ? booking.getEvent().getName() : null,
                booking.getUser() != null ? booking.getUser().getUserId() : null,
                booking.getUser() != null ? booking.getUser().getName() : null,
                booking.getBookedAt() != null ? booking.getBookedAt().toString() : null,
                booking.getStatus() != null ? booking.getStatus().name() : null);
    }

    private UserResponseDTO toUserResponse(User user) {
        String facultyOrOrganization;
        if (user instanceof za.ac.cput.domain.Student student) {
            facultyOrOrganization = student.getFaculty();
        } else if (user instanceof za.ac.cput.domain.Organizer organizer) {
            facultyOrOrganization = organizer.getOrganizationName();
        } else {
            facultyOrOrganization = null;
        }
        return new UserResponseDTO(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null,
                facultyOrOrganization,
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
                user.isEnabled() ? "ACTIVE" : "DISABLED");
    }
}
