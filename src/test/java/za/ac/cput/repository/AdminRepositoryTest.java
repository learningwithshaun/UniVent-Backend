package za.ac.cput.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.BookingStatusEnum;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Repository tests for admin count queries and event status filter (BE 05).
 * Uses an embedded H2 database so no external DB is required.
 */
@DataJpaTest
class AdminRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void countQueries_returnTotalsWithoutLoadingAll() {
        // given
        Organizer org = new Organizer();
        org.setName("Org");
        org.setEmail("org@example.com");
        org.setRole(RoleEnum.ORGANIZER);

        Event e1 = new Event();
        e1.setName("E1");
        e1.setDateTime(LocalDateTime.parse("2026-12-01T10:00:00"));
        e1.setCategory("Cat");
        e1.setStatus(EventStatusEnum.PENDING_APPROVAL);
        e1.setOrganizer(org);
        e1.setRegistrationCount(2);

        Event e2 = new Event();
        e2.setName("E2");
        e2.setDateTime(LocalDateTime.parse("2026-12-02T10:00:00"));
        e2.setCategory("Cat");
        e2.setStatus(EventStatusEnum.APPROVED);
        e2.setOrganizer(org);
        e2.setRegistrationCount(3);

        eventRepository.saveAll(List.of(e1, e2));

        Student s1 = new Student();
        s1.setName("Ada");
        s1.setEmail("ada@example.com");
        s1.setRole(RoleEnum.STUDENT);
        s1.setFaculty("Science");
        userRepository.save(s1);

        Booking b1 = new Booking();
        b1.setEvent(e1);
        b1.setUser(s1);
        b1.setBookedAt(LocalDateTime.parse("2026-11-01T09:00:00"));
        b1.setStatus(BookingStatusEnum.CONFIRMED);
        bookingRepository.save(b1);

        // when / then
        assertEquals(2L, eventRepository.count());
        assertEquals(1L, userRepository.countByRole(RoleEnum.STUDENT));
        assertEquals(1L, bookingRepository.count());
    }

    @Test
    void findByStatus_filtersPendingApproval() {
        Organizer org = new Organizer();
        org.setName("Org");
        org.setEmail("org@example.com");
        org.setRole(RoleEnum.ORGANIZER);

        Event pending = new Event();
        pending.setName("Pending");
        pending.setDateTime(LocalDateTime.parse("2026-12-01T10:00:00"));
        pending.setCategory("Cat");
        pending.setStatus(EventStatusEnum.PENDING_APPROVAL);
        pending.setOrganizer(org);

        Event approved = new Event();
        approved.setName("Approved");
        approved.setDateTime(LocalDateTime.parse("2026-12-02T10:00:00"));
        approved.setCategory("Cat");
        approved.setStatus(EventStatusEnum.APPROVED);
        approved.setOrganizer(org);

        eventRepository.saveAll(List.of(pending, approved));

        List<Event> result = eventRepository.findByStatus(EventStatusEnum.PENDING_APPROVAL, PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertEquals(EventStatusEnum.PENDING_APPROVAL, result.get(0).getStatus());
    }

    @Test
    void findTop5ByOrderByDateTimeDesc_limitsToFiveAndSortsRecentFirst() {
        Organizer org = new Organizer();
        org.setName("Org");
        org.setEmail("org@example.com");
        org.setRole(RoleEnum.ORGANIZER);

        List<Event> events = List.of(
                event("e1", "2026-12-01T10:00:00", org),
                event("e2", "2026-12-02T10:00:00", org),
                event("e3", "2026-12-03T10:00:00", org),
                event("e4", "2026-12-04T10:00:00", org),
                event("e5", "2026-12-05T10:00:00", org),
                event("e6", "2026-12-06T10:00:00", org)
        );
        eventRepository.saveAll(events);

        List<Event> recent = eventRepository.findTop5ByOrderByDateTimeDesc();

        assertEquals(5, recent.size());
        assertTrue(recent.get(0).getDateTime().isAfter(recent.get(1).getDateTime()));
    }

    @Test
    void findByRoleAndSearch_filtersByRoleAndSearchTerm() {
        Student s1 = new Student();
        s1.setName("Ada Student");
        s1.setEmail("ada@example.com");
        s1.setRole(RoleEnum.STUDENT);
        s1.setFaculty("Science");

        Student s2 = new Student();
        s2.setName("Bob Student");
        s2.setEmail("bob@example.com");
        s2.setRole(RoleEnum.STUDENT);
        s2.setFaculty("Engineering");

        Organizer o1 = new Organizer();
        o1.setName("Carol Organizer");
        o1.setEmail("carol@example.com");
        o1.setRole(RoleEnum.ORGANIZER);
        o1.setOrganizationName("Org X");

        userRepository.saveAll(List.of(s1, s2, o1));

        // role only
        var page = userRepository.findByRoleAndSearch(RoleEnum.STUDENT, null, PageRequest.of(0, 10));
        assertEquals(2, page.getTotalElements());

        // role + search by name
        page = userRepository.findByRoleAndSearch(RoleEnum.STUDENT, "Ada", PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals("ada@example.com", page.getContent().get(0).getEmail());

        // role + search by email
        page = userRepository.findByRoleAndSearch(RoleEnum.STUDENT, "bob@example.com", PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());

        // no students match organizer role
        page = userRepository.findByRoleAndSearch(RoleEnum.ORGANIZER, "Ada", PageRequest.of(0, 10));
        assertEquals(0, page.getTotalElements());
    }

    private static Event event(String id, String dateTime, Organizer organizer) {
        Event e = new Event();
        e.setEventId(id);
        e.setName("Event " + id);
        e.setDateTime(LocalDateTime.parse(dateTime));
        e.setCategory("Cat");
        e.setStatus(EventStatusEnum.APPROVED);
        e.setOrganizer(organizer);
        e.setRegistrationCount(1);
        return e;
    }
}
