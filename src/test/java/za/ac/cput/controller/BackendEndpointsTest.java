package za.ac.cput.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import za.ac.cput.domain.*;
import za.ac.cput.dto.*;
import za.ac.cput.security.*;
import za.ac.cput.service.*;
import org.springframework.data.domain.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes=za.ac.cput.univentbackend.UniventBackendApplication.class)
@AutoConfigureMockMvc
class BackendEndpointsTest {
 @Autowired MockMvc mvc; @Autowired JwtTokenService tokens;
 @MockitoBean IAdministratorService admin; @MockitoBean OrganizerEventService organizer;
 private String bearer(RoleEnum role){return za.ac.cput.security.JwtTestTokens.bearer(tokens,"user-1",role);}

 @Test void adminEndpointsAndErrors() throws Exception {
   when(admin.getStats()).thenReturn(new PlatformStatsDTO(4,8,12));
   mvc.perform(get("/api/admin/stats").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isOk()).andExpect(jsonPath("$.eventCount").value(4));
   mvc.perform(get("/api/admin/stats")).andExpect(status().isUnauthorized());
   mvc.perform(get("/api/admin/stats").header("Authorization",bearer(RoleEnum.STUDENT))).andExpect(status().isForbidden());
   when(admin.getEvents("ALL")).thenReturn(List.of(new AdminEventSummaryDTO("e1","Show","Org","2026-08-01T10:00:00","APPROVED",2)));
   mvc.perform(get("/api/admin/events").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isOk()).andExpect(jsonPath("$[0].status").value("APPROVED"));
   when(admin.getEvents("BAD")).thenThrow(new za.ac.cput.exception.InvalidAdminRequestException("bad status"));
   mvc.perform(get("/api/admin/events?status=BAD").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.timestamp").exists());
   when(admin.approveEvent("missing")).thenThrow(new za.ac.cput.exception.EventNotFoundException("missing"));
   mvc.perform(patch("/api/admin/events/missing/approve").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isNotFound());
   when(admin.approveEvent("approved")).thenThrow(new za.ac.cput.exception.EventStateConflictException("not pending"));
   mvc.perform(patch("/api/admin/events/approved/approve").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isConflict());
   when(admin.disableEvent("e1")).thenReturn(new AdminEventSummaryDTO("e1","Show","Org","2026-08-01T10:00:00","DISABLED",2));
   mvc.perform(patch("/api/admin/events/e1/disable").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DISABLED"));
   when(admin.disableEvent("missing")).thenThrow(new za.ac.cput.exception.EventNotFoundException("missing"));
   mvc.perform(patch("/api/admin/events/missing/disable").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isNotFound());
   when(admin.disableEvent("disabled")).thenThrow(new za.ac.cput.exception.EventStateConflictException("already disabled"));
   mvc.perform(patch("/api/admin/events/disabled/disable").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isConflict());
   when(admin.getUsers(null,null,PageRequest.of(0,20))).thenReturn(new PageImpl<>(List.of(new UserResponseDTO("u1","Ada","a@b.test","STUDENT","ACTIVE","2026-01-01T00:00:00"))));
   mvc.perform(get("/api/admin/users").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].passwordHash").doesNotExist());
   when(admin.getUsers(null,"NOPE",PageRequest.of(0,20))).thenThrow(new za.ac.cput.exception.InvalidAdminRequestException("bad role"));
   mvc.perform(get("/api/admin/users?role=NOPE").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isBadRequest());
   when(admin.disableUser("missing","user-1")).thenThrow(new za.ac.cput.exception.UserNotFoundException("missing"));
   mvc.perform(patch("/api/admin/users/missing/disable").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isNotFound());
   when(admin.disableUser("user-1","user-1")).thenThrow(new za.ac.cput.exception.SelfDisableException());
   mvc.perform(patch("/api/admin/users/user-1/disable").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isBadRequest());
   mvc.perform(get("/api/admin/users").header("Authorization",bearer(RoleEnum.ORGANIZER))).andExpect(status().isForbidden());
 }

 @Test void organizerEndpointScopesToTokenAndSupportsAdmin() throws Exception {
   when(organizer.list("user-1",EventStatus.APPROVED)).thenReturn(List.of(new OrganizerEventSummaryDTO("mine","My event",LocalDateTime.parse("2026-08-01T10:00:00"),"Hall",EventStatus.APPROVED,3)));
   mvc.perform(get("/api/organizers/me/events?status=APPROVED").header("Authorization",bearer(RoleEnum.ORGANIZER))).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value("mine"));
   verify(organizer).list("user-1",EventStatus.APPROVED);
   when(organizer.list(null,null)).thenReturn(List.of());
   mvc.perform(get("/api/organizers/me/events").header("Authorization",bearer(RoleEnum.ADMIN))).andExpect(status().isOk());
   mvc.perform(get("/api/organizers/me/events").header("Authorization",bearer(RoleEnum.STUDENT))).andExpect(status().isForbidden());
   mvc.perform(get("/api/organizers/me/events")).andExpect(status().isUnauthorized());
   mvc.perform(get("/api/organizers/me/events?status=UNKNOWN").header("Authorization",bearer(RoleEnum.ORGANIZER))).andExpect(status().isBadRequest());
 }

 @Test void noEndpointResponseContainsPasswordHash() throws Exception {
   String auth=bearer(RoleEnum.ADMIN);
   when(admin.getStats()).thenReturn(new PlatformStatsDTO(1,1,1));
   when(admin.getEvents("ALL")).thenReturn(List.of(new AdminEventSummaryDTO("e","Name","Org","2026-08-01T10:00:00","APPROVED",0)));
   when(admin.approveEvent("e")).thenReturn(new AdminEventSummaryDTO("e","Name","Org","2026-08-01T10:00:00","APPROVED",0));
   when(admin.disableEvent("e")).thenReturn(new AdminEventSummaryDTO("e","Name","Org","2026-08-01T10:00:00","DISABLED",0));
   var user=new UserResponseDTO("u","Ada","ada@example.test","STUDENT","ACTIVE","2026-01-01T00:00:00");
   when(admin.getUsers(null,null,PageRequest.of(0,20))).thenReturn(new PageImpl<>(List.of(user)));
   when(admin.disableUser("u","user-1")).thenReturn(user);
   when(organizer.list("user-1",null)).thenReturn(List.of(new OrganizerEventSummaryDTO("e","Name",LocalDateTime.parse("2026-08-01T10:00:00"),"Hall",EventStatus.APPROVED,0)));
   assertSafe(mvc.perform(get("/api/admin/stats").header("Authorization",auth)).andReturn().getResponse().getContentAsString());
   assertSafe(mvc.perform(get("/api/admin/events").header("Authorization",auth)).andReturn().getResponse().getContentAsString());
   assertSafe(mvc.perform(patch("/api/admin/events/e/approve").header("Authorization",auth)).andReturn().getResponse().getContentAsString());
   assertSafe(mvc.perform(patch("/api/admin/events/e/disable").header("Authorization",auth)).andReturn().getResponse().getContentAsString());
   assertSafe(mvc.perform(get("/api/admin/users").header("Authorization",auth)).andReturn().getResponse().getContentAsString());
   assertSafe(mvc.perform(patch("/api/admin/users/u/disable").header("Authorization",auth)).andReturn().getResponse().getContentAsString());
   assertSafe(mvc.perform(get("/api/organizers/me/events").header("Authorization",bearer(RoleEnum.ORGANIZER))).andReturn().getResponse().getContentAsString());
 }
 private void assertSafe(String response){org.junit.jupiter.api.Assertions.assertFalse(response.toLowerCase().contains("passwordhash"),response);}
}
