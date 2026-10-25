package za.ac.cput.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.Organizer;
import za.ac.cput.service.OrganizerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizerControllerTest {
    @Mock
    private OrganizerService service;

    @InjectMocks
    private OrganizerController controller;

    @Test
    void createOrganizerReturnsCreatedOrganizer() {
        Organizer organizer = new Organizer.Builder()
                .setOrganizationName("UniVent")
                .setContactEmail("info@univent.com")
                .build();
        when(service.create(organizer)).thenReturn(organizer);

        ResponseEntity<Organizer> response = controller.createOrganizer(organizer);

        assertNotNull(response.getBody());
        assertEquals("UniVent", response.getBody().getOrganizationName());
        verify(service).create(organizer);
    }

    @Test
    void getOrganizerByIdReturnsOrganizer() {
        Organizer organizer = new Organizer.Builder()
                .setOrganizationName("OrgName")
                .setContactEmail("contact@org.com")
                .build();
        when(service.read("org-1")).thenReturn(organizer);

        ResponseEntity<Organizer> response = controller.getOrganizerById("org-1");

        assertNotNull(response.getBody());
        assertEquals("OrgName", response.getBody().getOrganizationName());
        verify(service).read("org-1");
    }

    @Test
    void updateOrganizerReturnsUpdatedOrganizer() {
        Organizer organizer = new Organizer.Builder()
                .setOrganizationName("Updated")
                .build();
        when(service.update(organizer)).thenReturn(organizer);

        ResponseEntity<Organizer> response = controller.updateOrganizer(organizer);

        assertNotNull(response.getBody());
        assertEquals("Updated", response.getBody().getOrganizationName());
        verify(service).update(organizer);
    }

    @Test
    void deleteOrganizerReturnsNoContent() {
        ResponseEntity<Void> response = controller.delete("org-1");

        assertEquals(204, response.getStatusCode().value());
        verify(service).delete("org-1");
    }
}
