package za.ac.cput.service;

import za.ac.cput.domain.Administrator;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.User;
import za.ac.cput.dtos.PlatformStatsDTO;

import java.util.List;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

public interface IAdministratorService extends IService<Administrator, String> {
    Administrator findByAdministratorNumber(String administratorNumber);

    List<User> getAllUsers();

    List<Event> getAllEvents();

    Event approveEvent(Long eventId);

    Event disableEvent(Long eventId);

    User disableUser(Long userId);

    PlatformStatsDTO getPlatformStats();
}
