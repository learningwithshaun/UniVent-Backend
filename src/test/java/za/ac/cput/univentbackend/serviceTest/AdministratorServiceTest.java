package za.ac.cput.univentbackend.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import za.ac.cput.domain.Administrator;
import za.ac.cput.repository.AdministratorRepository;
import za.ac.cput.service.AdministratorService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdministratorServiceTest {

	@Mock
	private AdministratorRepository repository;

	@InjectMocks
	private AdministratorService service;

	@Test
	void testCreateAdministrator() {
		Administrator admin = new Administrator.Builder()
				.setAdminLevel("SUPER")
				.build();

		when(repository.save(any(Administrator.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		Administrator result = service.create(admin);

		assertNotNull(result);
		assertEquals("SUPER", result.getAdminLevel());
		verify(repository).save(any(Administrator.class));
	}

	@Test
	void testReadAdministrator() {
		Administrator admin = new Administrator.Builder()
				.setAdminLevel("LEVEL")
				.build();

		when(repository.findById("1")).thenReturn(Optional.of(admin));

		Administrator result = service.read("1");

		assertNotNull(result);
		assertEquals("LEVEL", result.getAdminLevel());
	}

	@Test
	void testUpdateAdministrator() {
		when(repository.save(any(Administrator.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		Administrator updated = new Administrator.Builder()
				.setAdminLevel("NEW")
				.build();

		Administrator result = service.update(updated);

		assertNotNull(result);
		assertEquals("NEW", result.getAdminLevel());
		verify(repository).save(any(Administrator.class));
	}

	@Test
	void testDeleteAdministrator() {
		doNothing().when(repository).deleteById("1");

		service.delete("1");

		verify(repository).deleteById("1");
	}

	@Test
	void testCreateNullAdministratorReturnsNull() {
		Administrator result = service.create(null);
		assertNull(result);
	}

	@Test
	void testUpdateNullAdministratorReturnsNull() {
		assertNull(service.update(null));
	}

	@Test
	void testFindByAdministratorNumberReturnsNull() {
		// The service method currently returns null - assert current behaviour.
		assertNull(service.findByAdministratorNumber("123"));
	}
}
