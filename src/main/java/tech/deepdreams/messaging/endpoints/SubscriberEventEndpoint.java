package tech.deepdreams.messaging.endpoints;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.requests.SubscriberCreationPayload;
import tech.deepdreams.messaging.services.SubscriberService;

@Log4j2
@Service
public class SubscriberEventEndpoint {
	private SubscriberService  subscriberService ;
	private ExecutorService executorService ;
	
	
	public SubscriberEventEndpoint (SubscriberService  subscriberService) {
		this.subscriberService = subscriberService ;
		this.executorService = Executors.newFixedThreadPool(3) ;
	}
	
	
	@Scheduled(fixedDelay = 30_000)
	public void handleEvent () {
		subscriberService.fetchMessagesFromQueue()
		           .forEach(message -> {
		        	   SubscriberCreationPayload payload = new SubscriberCreationPayload() ; 		        				  
	        		   payload.setEventId(message.getId()) ;
	        		   payload.setEmailAddress(message.getEmailAddress()) ;
	        		   log.info(String.format("Save reminder email : %s", payload)) ;
	        		   try {
		        		   subscriberService.saveReminderEmail(payload) ;
		        	   } catch (IOException e) {
		        		   log.error(String.format("Unable to save reminder email : %s", payload), e) ;
		        	   }
		           }) ;
	}
	
	
	@PreDestroy
	public void cleanUp() {
		executorService.shutdown(); 
	}
}
