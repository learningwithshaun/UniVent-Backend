package za.ac.cput.service;

import za.ac.cput.domain.Venue;

public interface IVenueService extends IService<Venue, String> {

    Venue findVenue(String venueName);

}