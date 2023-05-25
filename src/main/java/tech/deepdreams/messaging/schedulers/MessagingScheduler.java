package tech.deepdreams.messaging.schedulers;
import java.time.LocalTime;

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
	
	
	@Scheduled(fixedRate = 60_000) 
    public void scheduleEmails() {
		log.info(String.format("Scheduled Emails : Execution time %s", LocalTime.now())) ;
		reminderEmailService.fetchUndeliveredEmails()
			 .forEach(reminderEmail -> {
				 log.info(String.format("Email received %s", reminderEmail)) ;
				 reminderEmailService.sendReminderEmail(reminderEmail) ;
				 log.info(String.format("Email sent %s", reminderEmail)) ;
			 }) ; 
    }
}
