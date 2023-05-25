package tech.deepdreams.messaging.endpoints;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.services.ReminderEmailService;


@AllArgsConstructor
@RestController
@RequestMapping("/api/messaging")
public class ReminderEmailWebService {
	private ReminderEmailService reminderEmailService ;
	
	
	@GetMapping("/exists/{eventType}/{eventId}")
	public Optional<Boolean> existsByEventTypeAndEventId (@PathVariable("eventType")  String eventType, @PathVariable("eventId") Long eventId){
		return reminderEmailService.existsByEventTypeAndEventId(eventType, eventId) ;
	}

	
	@GetMapping("/delivered/{delivered}")
	public List<ReminderEmailDTO> fetchEmails(@PathVariable("delivered") Boolean delivered) {
		return reminderEmailService.fetchEmails(delivered) ;
	}
	
	
	@GetMapping("/period/{startDate}/{endDate}")
	public List<ReminderEmailDTO> fetchEmailsBetween(@PathVariable("startDate")  String startDateStr, @PathVariable("endDate")  String endDateStr) {
		LocalDate startDate = LocalDate.parse(endDateStr) ;
		LocalDate endDate   = LocalDate.parse(endDateStr) ;
		return reminderEmailService.fetchEmailsBetween(startDate, endDate) ;
	}	
}
