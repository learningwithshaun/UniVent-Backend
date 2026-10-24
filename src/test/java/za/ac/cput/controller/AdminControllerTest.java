package za.ac.cput.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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
import za.ac.cput.security.CurrentUserProvider;
import za.ac.cput.security.UserPrincipal;
import za.ac.cput.service.IAdminService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller-layer tests for the Administrator API (BE 05).
 * Covers success, 401, 403, 404, 400, 409 and validation for every /api/admin endpoint.
 */
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAdminService adminService;

    @MockBean
    private CurrentUserProvider currentUserProvider;

    private UserPrincipal admin;
    private UserPrincipal student;
    private UserPrincipal organizer;

    @BeforeEach
    void setUp() {
        admin = new UserPrincipal("admin-1", "admin@example.com", za.ac.cput.domain.RoleEnum.ADMIN);
        student = new UserPrincipal("student-1", "student@example.com", za.ac.cput.domain.RoleEnum.STUDENT);
        organizer = new UserPrincipal("organizer-1", "organizer@example.com", za.ac.cput.domain.RoleEnum.ORGANIZER);
    }

    /* ============================================================ auth helpers */

    private void asAdmin() {
        org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(admin);
    }

    private void asStudent() {
        org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(student);
    }

    private void asOrganizer() {
        org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(organizer);
    }

    private void asAnonymous() {
        org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(null);
    }

    /* ============================================================ GET /stats */

    @Test
    void stats_success_returnsTotals() throws Exception {
        asAdmin();
        PlatformStatsDTO stats = new PlatformStatsDTO(10, 50, 100, List.of(), List.of());
        when(adminService.getStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(10))
                .andExpect(jsonPath("$.totalUsers").value(50))
                .andExpect(jsonPath("$.totalBookings").value(100));
    }

    @Test
    void stats_401_noToken() throws Exception {
        asAnonymous();
        when(adminService.getStats()).thenThrow(new NotAuthenticatedException("No valid authentication token present"));

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void stats_403_student() throws Exception {
        asStudent();
        when(adminService.getStats()).thenThrow(new AdminAccessDeniedException("ADMIN role required"));

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void stats_403_organizer() throws Exception {
        asOrganizer();
        when(adminService.getStats()).thenThrow(new AdminAccessDeniedException("ADMIN role required"));

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());
    }

    /* ============================================================ GET /events */

    @Test
    void events_success_all() throws Exception {
        asAdmin();
        AdminEventSummaryDTO event = new AdminEventSummaryDTO(
                "evt-1", "Career Fair", "org@example.com",
                "2026-11-01T10:00:00", "Career", "PENDING_APPROVAL", 5, List.of("APPROVE", "DISABLE"));
        when(adminService.getEvents("ALL")).thenReturn(List.of(event));

        mockMvc.perform(get("/api/admin/events").param("status", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("evt-1"))
                .andExpect(jsonPath("$[0].name").value("Career Fair"))
                .andExpect(jsonPath("$[0].organizerName").value("org@example.com"))
                .andExpect(jsonPath("$[0].dateTime").value("2026-11-01T10:00:00"))
                .andExpect(jsonPath("$[0].category").value("Career"))
                .andExpect(jsonPath("$[0].status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$[0].registrationCount").value(5))
                .andExpect(jsonPath("$[0].allowedActions[0]").value("APPROVE"));
    }

    @Test
    void events_success_noFilterDefaultsAll() throws Exception {
        asAdmin();
        when(adminService.getEvents("ALL")).thenReturn(List.of());
        mockMvc.perform(get("/api/admin/events"))
                .andExpect(status().isOk());
    }

    @Test
    void events_401_noToken() throws Exception {
        asAnonymous();
        when(adminService.getEvents(any())).thenThrow(new NotAuthenticatedException("No valid authentication token present"));
        mockMvc.perform(get("/api/admin/events")).andExpect(status().isUnauthorized());
    }

    @Test
    void events_403_student() throws Exception {
        asStudent();
        when(adminService.getEvents(any())).thenThrow(new AdminAccessDeniedException("ADMIN role required"));
        mockMvc.perform(get("/api/admin/events")).andExpect(status().isForbidden());
    }

    @Test
    void events_400_unknownStatus() throws Exception {
        asAdmin();
        when(adminService.getEvents("BOGUS")).thenThrow(new InvalidAdminRequestException("Unknown status 'BOGUS'"));

        mockMvc.perform(get("/api/admin/events").param("status", "BOGUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    /* ============================================================ PATCH /events/{id}/approve */

    @Test
    void approveEvent_success() throws Exception {
        asAdmin();
        AdminEventSummaryDTO updated = new AdminEventSummaryDTO(
                "evt-1", "Career Fair", "org@example.com", "2026-11-01T10:00:00",
                "Career", "APPROVED", 5, List.of("DISABLE"));
        when(adminService.approveEvent("evt-1")).thenReturn(updated);

        mockMvc.perform(patch("/api/admin/events/evt-1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.id").value("evt-1"));
    }

    @Test
    void approveEvent_404_missingEvent() throws Exception {
        asAdmin();
        when(adminService.approveEvent("nope")).thenThrow(new EventNotFoundException("nope"));
        mockMvc.perform(patch("/api/admin/events/nope/approve"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void approveEvent_409_notPending() throws Exception {
        asAdmin();
        when(adminService.approveEvent("evt-1")).thenThrow(new EventStateConflictException("not pending"));
        mockMvc.perform(patch("/api/admin/events/evt-1/approve"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void approveEvent_401_noToken() throws Exception {
        asAnonymous();
        when(adminService.approveEvent(any())).thenThrow(new NotAuthenticatedException("No valid authentication token present"));
        mockMvc.perform(patch("/api/admin/events/evt-1/approve")).andExpect(status().isUnauthorized());
    }

    @Test
    void approveEvent_403_student() throws Exception {
        asStudent();
        when(adminService.approveEvent(any())).thenThrow(new AdminAccessDeniedException("ADMIN role required"));
        mockMvc.perform(patch("/api/admin/events/evt-1/approve")).andExpect(status().isForbidden());
    }

    /* ============================================================ PATCH /events/{id}/disable */

    @Test
    void disableEvent_success() throws Exception {
        asAdmin();
        AdminEventSummaryDTO updated = new AdminEventSummaryDTO(
                "evt-1", "Career Fair", "org@example.com", "2026-11-01T10:00:00",
                "Career", "DISABLED", 5, List.of());
        when(adminService.disableEvent("evt-1")).thenReturn(updated);

        mockMvc.perform(patch("/api/admin/events/evt-1/disable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISABLED"));
    }

    @Test
    void disableEvent_404_missingEvent() throws Exception {
        asAdmin();
        when(adminService.disableEvent("nope")).thenThrow(new EventNotFoundException("nope"));
        mockMvc.perform(patch("/api/admin/events/nope/disable"))
                .andExpect(status().isNotFound());
    }

    @Test
    void disableEvent_409_alreadyDisabled() throws Exception {
        asAdmin();
        when(adminService.disableEvent("evt-1")).thenThrow(new EventStateConflictException("already disabled"));
        mockMvc.perform(patch("/api/admin/events/evt-1/disable"))
                .andExpect(status().isConflict());
    }

    @Test
    void disableEvent_401_noToken() throws Exception {
        asAnonymous();
        when(adminService.disableEvent(any())).thenThrow(new NotAuthenticatedException("No valid authentication token present"));
        mockMvc.perform(patch("/api/admin/events/evt-1/disable")).andExpect(status().isUnauthorized());
    }

    @Test
    void disableEvent_403_student() throws Exception {
        asStudent();
        when(adminService.disableEvent(any())).thenThrow(new AdminAccessDeniedException("ADMIN role required"));
        mockMvc.perform(patch("/api/admin/events/evt-1/disable")).andExpect(status().isForbidden());
    }

    /* ============================================================ GET /users */

    @Test
    void users_success_page() throws Exception {
        asAdmin();
        UserResponseDTO user = new UserResponseDTO(
                "u-1", "Ada Student", "ada@example.com", "STUDENT",
                "Science", "2026-01-15T09:00:00", "ACTIVE");
        Page<UserResponseDTO> page = new PageImpl<>(List.of(user), PageRequest.of(0, 20), 1);
        when(adminService.getUsers(null, null, any())).thenReturn(page);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("u-1"))
                .andExpect(jsonPath("$.content[0].name").value("Ada Student"))
                .andExpect(jsonPath("$.content[0].email").value("ada@example.com"))
                .andExpect(jsonPath("$.content[0].role").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].facultyOrOrganization").value("Science"))
                .andExpect(jsonPath("$.content[0].joinDate").value("2026-01-15T09:00:00"))
                .andExpect(jsonPath("$.content[0].accountStatus").value("ACTIVE"));
    }

    @Test
    void users_401_noToken() throws Exception {
        asAnonymous();
        when(adminService.getUsers(any(), any(), any())).thenThrow(new NotAuthenticatedException("No valid authentication token present"));
        mockMvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
    }

    @Test
    void users_403_organizer() throws Exception {
        asOrganizer();
        when(adminService.getUsers(any(), any(), any())).thenThrow(new AdminAccessDeniedException("ADMIN role required"));
        mockMvc.perform(get("/api/admin/users")).andExpect(status().isForbidden());
    }

    @Test
    void users_400_unknownRole() throws Exception {
        asAdmin();
        when(adminService.getUsers("x", "BOGUS", any())).thenThrow(new InvalidAdminRequestException("Unknown role 'BOGUS'"));
        mockMvc.perform(get("/api/admin/users").param("role", "BOGUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    /* ============================================================ PATCH /users/{id}/disable */

    @Test
    void disableUser_success() throws Exception {
        asAdmin();
        UserResponseDTO updated = new UserResponseDTO(
                "u-2", "Old User", "old@example.com", "STUDENT",
                null, "2026-02-01T00:00:00", "DISABLED");
        when(adminService.disableUser("u-2", "admin-1")).thenReturn(updated);

        mockMvc.perform(patch("/api/admin/users/u-2/disable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("DISABLED"));
    }

    @Test
    void disableUser_404_missingUser() throws Exception {
        asAdmin();
        when(adminService.disableUser("nope", "admin-1")).thenThrow(new UserNotFoundException("nope"));
        mockMvc.perform(patch("/api/admin/users/nope/disable"))
                .andExpect(status().isNotFound());
    }

    @Test
    void disableUser_400_disableSelf() throws Exception {
        asAdmin();
        when(adminService.disableUser("admin-1", "admin-1")).thenThrow(new SelfDisableException());
        mockMvc.perform(patch("/api/admin/users/admin-1/disable"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void disableUser_401_noToken() throws Exception {
        asAnonymous();
        when(adminService.disableUser(any(), any())).thenThrow(new NotAuthenticatedException("No valid authentication token present"));
        mockMvc.perform(patch("/api/admin/users/u-2/disable")).andExpect(status().isUnauthorized());
    }

    @Test
    void disableUser_403_student() throws Exception {
        asStudent();
        when(adminService.disableUser(any(), any())).thenThrow(new AdminAccessDeniedException("ADMIN role required"));
        mockMvc.perform(patch("/api/admin/users/u-2/disable")).andExpect(status().isForbidden());
    }

    /* ============================================================ response shape (no sensitive fields) */

    @Test
    void userResponse_neverContainsPasswordOrTokenFields() throws Exception {
        asAdmin();
        UserResponseDTO user = new UserResponseDTO(
                "u-1", "Ada", "ada@example.com", "STUDENT",
                "Science", "2026-01-15T09:00:00", "ACTIVE");
        Page<UserResponseDTO> page = new PageImpl<>(List.of(user), PageRequest.of(0, 20), 1);
        when(adminService.getUsers(null, null, any())).thenReturn(page);

        String body = mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertFalse(body.contains("password"), "body must not contain 'password'");
        org.junit.jupiter.api.Assertions.assertFalse(body.contains("passwordHash"), "body must not contain 'passwordHash'");
        org.junit.jupiter.api.Assertions.assertFalse(body.contains("token"), "body must not contain 'token'");
    }

    @Test
    void verifyAdminServiceCalledForEveryRoute() throws Exception {
        asAdmin();
        when(adminService.getStats()).thenReturn(new PlatformStatsDTO(0, 0, 0, List.of(), List.of()));
        when(adminService.getEvents(any())).thenReturn(List.of());
        when(adminService.approveEvent(any())).thenReturn(new AdminEventSummaryDTO("x", "x", "x", "x", "x", "x", 0, List.of()));
        when(adminService.disableEvent(any())).thenReturn(new AdminEventSummaryDTO("x", "x", "x", "x", "x", "x", 0, List.of()));
        when(adminService.getUsers(any(), any(), any())).thenReturn(new PageImpl<>(List.of()));
        when(adminService.disableUser(any(), any())).thenReturn(new UserResponseDTO("x", "x", "x", "x", null, null, "ACTIVE"));

        mockMvc.perform(get("/api/admin/stats"));
        mockMvc.perform(get("/api/admin/events"));
        mockMvc.perform(patch("/api/admin/events/1/approve"));
        mockMvc.perform(patch("/api/admin/events/1/disable"));
        mockMvc.perform(get("/api/admin/users"));
        mockMvc.perform(patch("/api/admin/users/1/disable"));

        verify(adminService).getStats();
        verify(adminService).getEvents(any());
        verify(adminService).approveEvent(any());
        verify(adminService).disableEvent(any());
        verify(adminService).getUsers(any(), any(), any());
        verify(adminService).disableUser(any(), any());
    }
}
