package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Administrator;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.User;
import za.ac.cput.dtos.PlatformStatsDTO;
import za.ac.cput.repository.AdministratorRepository;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.UserRepository;

import java.util.List;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

@Service
public class AdministratorService implements IAdministratorService{
    private final AdministratorRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public AdministratorService(AdministratorRepository repository,
                                UserRepository userRepository,
                                EventRepository eventRepository,
                                BookingRepository bookingRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Administrator findByAdministratorNumber(String administratorNumber) {
        return null;
    }

    @Override
    public Administrator create(Administrator administrator) {
        if (administrator == null) {
            return null;
        }
        return  repository.save(administrator);
    }

    @Override
    public Administrator read(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Administrator update(Administrator administrator) {
        if(administrator == null) {
            return null;
        }
        return repository.save(administrator);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event approveEvent(Long eventId) {
        Event event = eventRepository.findById(eventId.intValue()).orElse(null);
        if (event != null) {
            event.setStatus(EventStatusEnum.APPROVED);
            return eventRepository.save(event);
        }
        return null;
    }

    @Override
    public Event disableEvent(Long eventId) {
        Event event = eventRepository.findById(eventId.intValue()).orElse(null);
        if (event != null) {
            event.setStatus(EventStatusEnum.DISABLED);
            return eventRepository.save(event);
        }
        return null;
    }

    @Override
    public User disableUser(Long userId) {
        User user = userRepository.findById(userId.toString()).orElse(null);
        if (user != null) {
            user.setDisabled(true);
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public PlatformStatsDTO getPlatformStats() {
        long totalEventCount = eventRepository.count();
        long totalUserCount = userRepository.count();
        long totalBookingCount = bookingRepository.count();
        return new PlatformStatsDTO(totalEventCount, totalUserCount, totalBookingCount);
    }
}
