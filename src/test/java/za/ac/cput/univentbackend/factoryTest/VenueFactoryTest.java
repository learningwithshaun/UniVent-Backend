package za.ac.cput.univentbackend.factoryTest;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Venue;
import za.ac.cput.factory.VenueFactory;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Student Name: Sesethu Nciti
 * Student Number: 231118384
 */

public class VenueFactoryTest {

    @Test
    public void testCreateVenue() {

        Venue venue = VenueFactory.createVenue(
                "CPUT Hall",
                "Bellville Campus",
                "Ground Floor, Block A",
                500,
                new ArrayList<>()
        );

        assertNotNull(venue);
        assertEquals("CPUT Hall", venue.getVenueName());
        assertEquals("Bellville Campus", venue.getCampus());
        assertEquals("Ground Floor, Block A", venue.getLocationDetails());
        assertEquals(500, venue.getCapacity());
    }

    @Test
    public void testCreateVenueWithNullName() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                VenueFactory.createVenue(
                        null,
                        "Bellville Campus",
                        "Ground Floor, Block A",
                        500,
                        new ArrayList<>()
                ));

        assertEquals("Venue name is required", exception.getMessage());
    }

    @Test
    public void testCreateVenueWithNullCampus() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                VenueFactory.createVenue(
                        "CPUT Hall",
                        null,
                        "Ground Floor, Block A",
                        500,
                        new ArrayList<>()
                ));

        assertEquals("Campus is required", exception.getMessage());
    }

    @Test
    public void testCreateVenueWithNullLocationDetails() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                VenueFactory.createVenue(
                        "CPUT Hall",
                        "Bellville Campus",
                        null,
                        500,
                        new ArrayList<>()
                ));

        assertEquals("Location details are required", exception.getMessage());
    }

    @Test
    public void testCreateVenueWithInvalidCapacity() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                VenueFactory.createVenue(
                        "CPUT Hall",
                        "Bellville Campus",
                        "Ground Floor, Block A",
                        0,
                        new ArrayList<>()
                ));

        assertEquals("Capacity must be greater than 0", exception.getMessage());
    }
}