package za.ac.cput.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.Organizer;
import za.ac.cput.repository.OrganizerRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizerServiceTest {
    @Mock
    private OrganizerRepository repository;

    @InjectMocks
    private OrganizerService service;

    @Test
    void createOrganizerSavesAndReturnsOrganizer() {
        Organizer organizer = organizer("UniVent");
        when(repository.save(organizer)).thenReturn(organizer);

        assertSame(organizer, service.create(organizer));
        verify(repository).save(organizer);
    }

    @Test
    void readOrganizerReturnsMatchingRecord() {
        Organizer organizer = organizer("UniVent");
        when(repository.findById("org-1")).thenReturn(Optional.of(organizer));

        assertSame(organizer, service.read("org-1"));
        verify(repository).findById("org-1");
    }

    @Test
    void readMissingOrganizerReturnsNull() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertNull(service.read("missing"));
    }

    @Test
    void updateOrganizerSavesAndReturnsOrganizer() {
        Organizer organizer = organizer("Updated");
        when(repository.save(organizer)).thenReturn(organizer);

        assertEquals("Updated", service.update(organizer).getOrganizationName());
        verify(repository).save(organizer);
    }

    @Test
    void deleteOrganizerDeletesById() {
        service.delete("org-1");

        verify(repository).deleteById("org-1");
    }

    @Test
    void nullCreateAndUpdateReturnNull() {
        assertNull(service.create(null));
        assertNull(service.update(null));
    }

    private Organizer organizer(String name) {
        return new Organizer.Builder()
                .setUserId("org-1")
                .setOrganizationName(name)
                .build();
    }
}
