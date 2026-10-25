package za.ac.cput.univentbackend.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.Venue;
import za.ac.cput.repository.VenueRepository;
import za.ac.cput.service.VenueService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Student Name: Sesethu Nciti
 * Student Number: 231118384
 */

@ExtendWith(MockitoExtension.class)
public class VenueServiceTest {

    @Mock
    private VenueRepository repository;

    @InjectMocks
    private VenueService service;

    @Test
    void testCreateVenue() {

        Venue venue = new Venue.Builder()
                .setVenueName("CPUT Hall")
                .setCampus("Bellville Campus")
                .setLocationDetails("Ground Floor, Block A")
                .setCapacity(500)
                .build();

        when(repository.save(venue)).thenReturn(venue);

        Venue created = service.create(venue);

        assertNotNull(created);
        assertEquals("CPUT Hall", created.getVenueName());
        assertEquals("Bellville Campus", created.getCampus());

        verify(repository).save(venue);
    }

    @Test
    void testReadVenue() {

        Venue venue = new Venue.Builder()
                .setVenueName("Main Hall")
                .setCampus("Cape Town Campus")
                .setLocationDetails("First Floor")
                .setCapacity(300)
                .build();

        when(repository.findById("V1")).thenReturn(Optional.of(venue));

        Venue found = service.read("V1");

        assertNotNull(found);
        assertEquals("Main Hall", found.getVenueName());

        verify(repository).findById("V1");
    }

    @Test
    void testUpdateVenue() {

        Venue venue = new Venue.Builder()
                .setVenueName("Updated Hall")
                .setCampus("District Six Campus")
                .setLocationDetails("Second Floor")
                .setCapacity(700)
                .build();

        when(repository.save(venue)).thenReturn(venue);

        Venue updated = service.update(venue);

        assertNotNull(updated);
        assertEquals("Updated Hall", updated.getVenueName());
        assertEquals(700, updated.getCapacity());

        verify(repository).save(venue);
    }

    @Test
    void testDeleteVenue() {

        service.delete("V1");

        verify(repository).deleteById("V1");
    }

    @Test
    void testCreateVenueWithNull() {

        Venue venue = service.create(null);

        assertNull(venue);

        verify(repository, never()).save(any(Venue.class));
    }

    @Test
    void testUpdateVenueWithNull() {

        Venue venue = service.update(null);

        assertNull(venue);

        verify(repository, never()).save(any(Venue.class));
    }
}