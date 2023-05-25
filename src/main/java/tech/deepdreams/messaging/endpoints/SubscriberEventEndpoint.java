package tech.deepdreams.messaging.endpoints;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.requests.SubscriberCreationPayload;
import tech.deepdreams.messaging.services.ReminderEmailService;
import tech.deepdreams.messaging.services.SubscriberService;

@Log4j2
@Service
public class SubscriberEventEndpoint {
	private SubscriberService  subscriberService ;
	private ReminderEmailService reminderEmailService ;
	private ExecutorService executorService ;
	
	
	public SubscriberEventEndpoint (SubscriberService  subscriberService) {
		this.subscriberService = subscriberService ;
		this.executorService = Executors.newFixedThreadPool(3) ;
	}
	
	
	@Scheduled(fixedDelay = 30_000)
	public void handleCreatedEvent () {
		log.info(String.format("SubscriberEventEndpoint.handleCreatedEvent : Execution time %s", OffsetDateTime.now())) ;
		subscriberService.fetchMessagesFromQueue()
		           .forEach(message -> {
		        	   log.info(String.format("Message received %s", message)) ;
		        	   SubscriberCreationPayload payload = new SubscriberCreationPayload() ;
	        		   payload.setEventId(message.getId()) ;
	        		   payload.setFirstName(message.getFirstName()) ;
	        		   payload.setLastName(message.getLastName()) ;
	        		   payload.setLabel(message.getLabel()) ;
	        		   payload.setEmailAddress(message.getEmailAddress()) ;
	        		   log.info(String.format("Save reminder email : %s", payload)) ;
	        		   try {
	        			   reminderEmailService.saveReminderEmail(payload) ;
		        	   } catch (Exception e) {
		        		   log.error(String.format("Unable to save reminder email : %s", payload), e) ;
		        	   }
		           }) ;
	}
	
	
	@PreDestroy
	public void cleanUp() {
		executorService.shutdown(); 
	}
}
