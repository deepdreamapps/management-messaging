package tech.deepdreams.messaging.endpoints;
import java.time.ZonedDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import tech.deepdreams.messaging.services.ReminderEmailService;

@AllArgsConstructor
@RestController
public class ProbesEndpoint {
	private ReminderEmailService reminderEmailService ;
	
	@GetMapping(path = "/api/messaging/liveness")
	public String livenessProbe () {
		return  String.format("Alive at %s", ZonedDateTime.now()) ;
	}
	
	@GetMapping(path = "/api/messaging/readiness")
	public String readinessProbe () {
		int number = reminderEmailService.fetchUndeliveredEmails().size() ;
		return String.format("Ready at %s %d", ZonedDateTime.now(), number) ; 
	}
}
