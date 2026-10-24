package za.ac.cput.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.BookingStatusEnum;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.User;
import za.ac.cput.dto.AdminEventSummaryDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Service-layer tests for AdminService (BE 05): count queries, status filter,
 * approve/disable transitions, user disable incl. self-disable, and role enforcement.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private AdminService adminService;

    private UserPrincipal admin;
    private UserPrincipal student;

    @BeforeEach
    void setUp() {
        admin = new UserPrincipal("admin-1", "admin@example.com", RoleEnum.ADMIN);
        student = new UserPrincipal("student-1", "student@example.com", RoleEnum.STUDENT);
    }

    /* ============================================================ role enforcement */

    @Test
    void getStats_requiresAdmin() {
        when(currentUserProvider.getCurrentUser()).thenReturn(student);
        assertThrows(AdminAccessDeniedException.class, () -> adminService.getStats());
    }

    @Test
    void getStats_401_noToken() {
        when(currentUserProvider.getCurrentUser()).thenReturn(null);
        assertThrows(NotAuthenticatedException.class, () -> adminService.getStats());
    }

    @Test
    void getUsers_requiresAdmin() {
        when(currentUserProvider.getCurrentUser()).thenReturn(student);
        assertThrows(AdminAccessDeniedException.class, () -> adminService.getUsers(null, null, PageRequest.of(0, 10)));
    }

    /* ============================================================ stats count queries */

    @Test
    void getStats_usesCountQueries_notLoadAll() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        when(eventRepository.count()).thenReturn(42L);
        when(userRepository.count()).thenReturn(100L);
        when(bookingRepository.count()).thenReturn(250L);
        when(eventRepository.findTop5ByOrderByDateTimeDesc()).thenReturn(List.of());
        when(bookingRepository.findTop5ByOrderByBookedAtDesc()).thenReturn(List.of());

        PlatformStatsDTO stats = adminService.getStats();

        assertEquals(42L, stats.totalEvents());
        assertEquals(100L, stats.totalUsers());
        assertEquals(250L, stats.totalBookings());
        // count() called exactly once each -> no loading into memory
        verify(eventRepository, times(1)).count();
        verify(userRepository, times(1)).count();
        verify(bookingRepository, times(1)).count();
    }

    @Test
    void getStats_recentListsLimitedToFive() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        when(eventRepository.count()).thenReturn(0L);
        when(userRepository.count()).thenReturn(0L);
        when(bookingRepository.count()).thenReturn(0L);
        List<Event> eightEvents = List.of(newEvent("e1"), newEvent("e2"), newEvent("e3"), newEvent("e4"),
                newEvent("e5"), newEvent("e6"), newEvent("e7"), newEvent("e8"));
        when(eventRepository.findTop5ByOrderByDateTimeDesc()).thenReturn(eightEvents.subList(0, 5));
        when(bookingRepository.findTop5ByOrderByBookedAtDesc()).thenReturn(List.of());

        PlatformStatsDTO stats = adminService.getStats();

        assertEquals(5, stats.recentEvents().size());
    }

    /* ============================================================ event status filter */

    @Test
    void getEvents_returnsAllWhenStatusNullOrAll() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        when(eventRepository.findAll()).thenReturn(List.of());
        assertEquals(List.of(), adminService.getEvents(null));
        assertEquals(List.of(), adminService.getEvents("ALL"));
        assertEquals(List.of(), adminService.getEvents(""));
    }

    @Test
    void getEvents_filtersByStatus() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        List<Event> pending = List.of(newEvent("e1"));
        when(eventRepository.findByStatus(EventStatusEnum.PENDING_APPROVAL, any(Pageable.class))).thenReturn(pending);

        List<AdminEventSummaryDTO> result = adminService.getEvents("PENDING_APPROVAL");

        assertEquals(1, result.size());
        assertEquals("PENDING_APPROVAL", result.get(0).status());
    }

    @Test
    void getEvents_400_unknownStatus() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        assertThrows(InvalidAdminRequestException.class, () -> adminService.getEvents("BOGUS"));
    }

    /* ============================================================ approve transition */

    @Test
    void approveEvent_transitionsPendingToApproved() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        Event event = newEvent("e1");
        event.setStatus(EventStatusEnum.PENDING_APPROVAL);
        when(eventRepository.findById("e1")).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AdminEventSummaryDTO result = adminService.approveEvent("e1");

        assertEquals(EventStatusEnum.APPROVED, event.getStatus());
        assertEquals("APPROVED", result.status());
    }

    @Test
    void approveEvent_404_whenMissing() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        when(eventRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> adminService.approveEvent("missing"));
    }

    @Test
    void approveEvent_409_whenNotPending() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        Event event = newEvent("e1");
        event.setStatus(EventStatusEnum.APPROVED);
        when(eventRepository.findById("e1")).thenReturn(Optional.of(event));
        assertThrows(EventStateConflictException.class, () -> adminService.approveEvent("e1"));
    }

    /* ============================================================ disable transition */

    @Test
    void disableEvent_transitionsToDisabled() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        Event event = newEvent("e1");
        event.setStatus(EventStatusEnum.APPROVED);
        when(eventRepository.findById("e1")).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AdminEventSummaryDTO result = adminService.disableEvent("e1");

        assertEquals(EventStatusEnum.DISABLED, event.getStatus());
        assertEquals("DISABLED", result.status());
    }

    @Test
    void disableEvent_404_whenMissing() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        when(eventRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> adminService.disableEvent("missing"));
    }

    @Test
    void disableEvent_409_whenAlreadyDisabled() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        Event event = newEvent("e1");
        event.setStatus(EventStatusEnum.DISABLED);
        when(eventRepository.findById("e1")).thenReturn(Optional.of(event));
        assertThrows(EventStateConflictException.class, () -> adminService.disableEvent("e1"));
    }

    /* ============================================================ users */

    @Test
    void getUsers_mapsWithoutPassword() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        Student studentUser = new Student();
        studentUser.setUserId("u-1");
        studentUser.setName("Ada");
        studentUser.setEmail("ada@example.com");
        studentUser.setRole(RoleEnum.STUDENT);
        studentUser.setFaculty("Science");
        studentUser.setCreatedAt(LocalDateTime.parse("2026-01-15T09:00:00"));
        studentUser.setEnabled(true);
        Page<User> page = new PageImpl<>(List.of(studentUser));
        when(userRepository.findByRoleAndSearch(null, null, any(Pageable.class))).thenReturn(page);

        Page<UserResponseDTO> result = adminService.getUsers(null, null, PageRequest.of(0, 20));

        assertFalse(result.isEmpty());
        UserResponseDTO dto = result.getContent().get(0);
        assertEquals("u-1", dto.id());
        assertEquals("Ada", dto.name());
        assertEquals("ada@example.com", dto.email());
        assertEquals("STUDENT", dto.role());
        assertEquals("Science", dto.facultyOrOrganization());
        assertEquals("2026-01-15T09:00:00", dto.joinDate());
        assertEquals("ACTIVE", dto.accountStatus());
        // never exposed
        org.junit.jupiter.api.Assertions.assertFalse(dto.toString().contains("password"));
    }

    @Test
    void getUsers_400_unknownRole() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        assertThrows(InvalidAdminRequestException.class,
                () -> adminService.getUsers(null, "BOGUS", PageRequest.of(0, 10)));
    }

    /* ============================================================ disable user */

    @Test
    void disableUser_disablesAccount() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        User user = new User() {};
        user.setUserId("u-2");
        user.setEnabled(true);
        when(userRepository.findById("u-2")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO result = adminService.disableUser("u-2", "admin-1");

        assertFalse(user.isEnabled());
        assertEquals("DISABLED", result.accountStatus());
    }

    @Test
    void disableUser_404_whenMissing() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        when(userRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.disableUser("missing", "admin-1"));
    }

    @Test
    void disableUser_400_disableSelf() {
        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        assertThrows(SelfDisableException.class, () -> adminService.disableUser("admin-1", "admin-1"));
        verify(userRepository, never()).findById("admin-1");
    }

    /* ============================================================ helpers */

    private Event newEvent(String id) {
        Event event = new Event();
        event.setEventId(id);
        event.setName("Event " + id);
        event.setDateTime(LocalDateTime.parse("2026-11-01T10:00:00"));
        event.setCategory("Cat");
        event.setStatus(EventStatusEnum.PENDING_APPROVAL);
        event.setRegistrationCount(3);
        Organizer org = new Organizer();
        org.setName("Organizer");
        event.setOrganizer(org);
        return event;
    }

    private Booking newBooking(String id) {
        Booking booking = new Booking();
        booking.setBookingId(id);
        return booking;
    }
}
