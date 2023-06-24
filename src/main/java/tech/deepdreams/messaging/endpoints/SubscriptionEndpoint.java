package tech.deepdreams.messaging.endpoints;

import java.time.OffsetDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.services.ReminderEmailService;
import tech.deepdreams.messaging.services.SubscriptionService;

@Log4j2
@AllArgsConstructor
@Service
public class SubscriptionEndpoint {
	private SubscriptionService subscriptionService;
	private ReminderEmailService reminderEmailService ;
	
	

	@Scheduled(fixedDelay = 30_000)
	public void handleCreation() {
		log.info(String.format("SubscriptionEndpoint.handleCreation : Execution time %s", OffsetDateTime.now()));
		subscriptionService.fetchFromCreatedQueue().stream()
		   .map(event  -> {
			   return subscriptionService.fetchSubscription(event.getSubscriptionId()) ;
		   })
		   .map(subscription  -> {
			   ReminderEmailDTO reminderEmailDTO = null ;
			   try {
				   reminderEmailDTO = subscriptionService.genReminderEmail(subscription) ;
			   } catch (Exception e) {
				   log.error(String.format("Unable to generate reminder email from : %s", subscription), e) ;
			   }
			   return reminderEmailDTO ;
		   })
		   .forEach(reminderEmailDTO -> {

			   try {
				   reminderEmailService.sendReminderEmail(reminderEmailDTO) ;
			   } catch (Exception e) {
				   log.error(String.format("Unable to send reminder email : %s", reminderEmailDTO), e) ;
				   return ;
			   }
        }) ;
	}
}
