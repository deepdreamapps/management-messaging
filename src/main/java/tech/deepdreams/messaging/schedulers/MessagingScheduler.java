package tech.deepdreams.messaging.schedulers;
import java.time.OffsetDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.services.ReminderEmailService;

@Log4j2
@AllArgsConstructor
@Service
public class MessagingScheduler {
	private ReminderEmailService reminderEmailService ;
	
	
	@Scheduled(fixedRate = 30_000) 
    public void scheduleEmails() {
		log.info(String.format("MessagingScheduler.scheduleEmails : Execution time %s", OffsetDateTime.now())) ;
		reminderEmailService.fetchUndeliveredEmails()
			 .forEach(reminderEmail -> {
				 log.info(String.format("MessagingScheduler.scheduleEmails : Undelivered email found %s", reminderEmail)) ;
				 reminderEmailService.sendReminderEmail(reminderEmail) ;
				 log.info(String.format("Email sent %s", reminderEmail)) ;
			 }) ; 
    }
}
