package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.EventStatus;
import za.ac.cput.dto.OrganizerEventSummaryDTO;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import java.util.List;

@Service
public class OrganizerEventService {
    private final EventRepository events; private final BookingRepository bookings;
    public OrganizerEventService(EventRepository events, BookingRepository bookings) { this.events=events; this.bookings=bookings; }
    public List<OrganizerEventSummaryDTO> list(String organizerId, EventStatus status) {
        return events.findByOrganizerIdAndOptionalStatus(organizerId,status).stream()
            .map(e->new OrganizerEventSummaryDTO(e.getId(),e.getName(),e.getDateTime(),e.getVenueName(),e.getStatus(),bookings.countByEvent_Id(e.getId()))).toList();
    }
}
