package za.ac.cput.factory;

import za.ac.cput.domain.Event;
import za.ac.cput.domain.Venue;
import za.ac.cput.util.Helper;

import java.util.List;

/**
 * Name: Sesethu
 * Surname: Nciti
 * Student Number: 231118384
 */

public class VenueFactory {

    public static Venue createVenue(String venueName,
                                    String campus,
                                    String locationDetails,
                                    int capacity,
                                    List<Event> events) {

        if (Helper.isNullOrEmpty(venueName)) {
            throw new IllegalArgumentException("Venue name is required");
        }

        if (Helper.isNullOrEmpty(campus)) {
            throw new IllegalArgumentException("Campus is required");
        }

        if (Helper.isNullOrEmpty(locationDetails)) {
            throw new IllegalArgumentException("Location details are required");
        }

        if (!Helper.isPositive(capacity)) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }

        return new Venue.Builder()
                .setVenueName(venueName)
                .setCampus(campus)
                .setLocationDetails(locationDetails)
                .setCapacity(capacity)
                .setEvents(events)
                .build();
    }
}