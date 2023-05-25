package tech.deepdreams.messaging.repositories;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import tech.deepdreams.messaging.models.ReminderEmail;

public interface ReminderEmailRepository extends CrudRepository<ReminderEmail, Long>{

	public List<ReminderEmail> findBySent (boolean sent) ;
	
	public List<ReminderEmail> findByInstantBetween (OffsetDateTime startDate, OffsetDateTime endDate) ;
	
	public Optional<Boolean> existsByEventTypeAndEventId (String eventType, Long eventId) ;
}
