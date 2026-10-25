package za.ac.cput.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import za.ac.cput.domain.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ContextConfiguration(classes=za.ac.cput.univentbackend.UniventBackendApplication.class)
class EventRepositoryFilterTest {
 @Autowired EventRepository events; @Autowired OrganizerRepository organizers;
 @Test void filtersByOwnerAndOptionalStatus(){
   Organizer a=new Organizer();a.setUserId("org-a");a.setName("A");a.setRole(RoleEnum.ORGANIZER); organizers.save(a);
   Organizer b=new Organizer();b.setUserId("org-b");b.setName("B");b.setRole(RoleEnum.ORGANIZER); organizers.save(b);
   events.saveAll(List.of(event("a1",a,EventStatus.APPROVED),event("a2",a,EventStatus.DISABLED),event("b1",b,EventStatus.APPROVED)));
   assertEquals(List.of("a1","a2"),events.findByOrganizerIdAndOptionalStatus("org-a",null).stream().map(Event::getId).sorted().toList());
   assertEquals(List.of("a1"),events.findByOrganizerIdAndOptionalStatus("org-a",EventStatus.APPROVED).stream().map(Event::getId).toList());
 }
 private Event event(String id,Organizer owner,EventStatus status){return new Event(id,id,"",LocalDateTime.parse("2026-08-01T10:00:00"),10,status,owner,"Hall");}
}
