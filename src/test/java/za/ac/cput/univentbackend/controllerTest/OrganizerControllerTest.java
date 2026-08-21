package za.ac.cput.univentbackend.controllerTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import za.ac.cput.controller.OrganizerController;
import za.ac.cput.domain.Organizer;
import za.ac.cput.service.OrganizerService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganizerControllerTest {

	@Mock
	private OrganizerService service;

	@InjectMocks
	private OrganizerController controller;

	@Test
	void testCreateOrganizer() {
		Organizer org = new Organizer.Builder()
				.setOrganizationName("UniVent")
				.setContactEmail("info@univent.com")
				.build();

		when(service.create(org)).thenReturn(org);

		ResponseEntity<Organizer> response = controller.createOrganizer(org);

		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals("UniVent", response.getBody().getOrganizationName());
		verify(service).create(org);
	}

	@Test
	void testGetOrganizerById() {
		Organizer org = new Organizer.Builder()
				.setOrganizationName("OrgName")
				.setContactEmail("contact@org.com")
				.build();

		when(service.read("1")).thenReturn(org);

		ResponseEntity<Organizer> response = controller.getOrganizerById("1");

		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals("OrgName", response.getBody().getOrganizationName());
		verify(service).read("1");
	}

	@Test
	void testUpdateOrganizer() {
		Organizer org = new Organizer.Builder()
				.setOrganizationName("Updated")
				.build();

		when(service.update(org)).thenReturn(org);

		ResponseEntity<Organizer> response = controller.updateOrganizer(org);

		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals("Updated", response.getBody().getOrganizationName());
		verify(service).update(org);
	}

	@Test
	void testDeleteOrganizer() {
		ResponseEntity<Void> response = controller.delete("1");

		assertNotNull(response);
		assertEquals(204, response.getStatusCode().value());
		verify(service).delete("1");
	}
}
