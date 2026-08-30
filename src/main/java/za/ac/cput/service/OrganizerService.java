package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.Organizer;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.OrganizerRepository;
import za.ac.cput.util.UnauthorizedException;

import java.util.List;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

@Service
public class OrganizerService implements IOrganizerService{
    private final OrganizerRepository repository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public OrganizerService(OrganizerRepository repository,
                            EventRepository eventRepository,
                            BookingRepository bookingRepository) {
        this.repository = repository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Organizer findOrganizer(String organizerNumber) {
        return null;
    }

    @Override
    public Organizer create(Organizer organizer) {
        if (organizer == null) {
            return null;
        }
        return repository.save(organizer);
    }

    @Override
    public Organizer read(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Organizer update(Organizer organizer) {
        if (organizer == null) {
            return null;
        }
        return repository.save(organizer);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<Event> getMyEvents(Long organizerId) {
        Organizer organizer = repository.findById(organizerId.toString()).orElse(null);
        if (organizer == null) {
            return List.of();
        }
        return eventRepository.findByOrganizer(organizer);
    }

    @Override
    public List<Booking> getEventRegistrations(Long eventId, Long organizerId) {
        Event event = eventRepository.findById(eventId.intValue()).orElse(null);
        if (event == null) {
            throw new UnauthorizedException("Event not found");
        }

        Organizer organizer = repository.findById(organizerId.toString()).orElse(null);
        if (organizer == null) {
            throw new UnauthorizedException("Organizer not found");
        }

        if (event.getOrganizer() == null || !event.getOrganizer().getUserId().equals(organizer.getUserId())) {
            throw new UnauthorizedException("Organizer does not own this event");
        }

        return bookingRepository.findByEvent(event);
    }
}
