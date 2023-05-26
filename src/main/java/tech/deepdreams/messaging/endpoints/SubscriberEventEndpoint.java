package tech.deepdreams.messaging.endpoints;
import java.time.OffsetDateTime;
import javax.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.dtos.SubscriberDTO;
import tech.deepdreams.messaging.requests.SubscriberCreationPayload;
import tech.deepdreams.messaging.requests.SubscriberSuspensionPayload;
import tech.deepdreams.messaging.services.ReminderEmailService;
import tech.deepdreams.messaging.services.SubscriberService;

@Log4j2
@AllArgsConstructor
@Service
public class SubscriberEventEndpoint {
	private SubscriberService  subscriberService ;
	private ReminderEmailService reminderEmailService ;
	
	@PostConstruct
	public void init () {
		
	}
	
	
	@Scheduled(fixedDelay = 30_000)
	public void handleCreatedEvent () {
		log.info(String.format("SubscriberEventEndpoint.handleCreatedEvent : Execution time %s", OffsetDateTime.now())) ;
		subscriberService.fetchFromCreatedQueue()
		           .forEach(message -> {
		        	   log.info(String.format("Message received %s", message)) ;
		        	   SubscriberCreationPayload payload = new SubscriberCreationPayload() ;
		        	   payload.setId(message.getSubscriberId()) ;
	        		   payload.setFirstName(message.getFirstName()) ;
	        		   payload.setLastName(message.getLastName()) ;
	        		   payload.setLabel(message.getLabel()) ;
	        		   payload.setEmailAddress(message.getEmailAddress()) ;
	        		   log.info(String.format("Save reminder email : %s", payload)) ;
	        		   
	        		   ReminderEmailDTO reminderEmailDTO = null ;
	        		   try {
	        			   reminderEmailDTO = subscriberService.genReminderEmail(payload) ;
		        	   } catch (Exception e) {
		        		   log.error(String.format("Unable to generate reminder email : %s", payload), e) ;
		        		   return ;
		        	   }
	        		   
	        		   try {
	        			   reminderEmailService.sendReminderEmail(reminderEmailDTO) ;
		        	   } catch (Exception e) {
		        		   log.error(String.format("Unable to send reminder email : %s", reminderEmailDTO), e) ;
		        		   return ;
		        	   }
		           }) ;
	}
	
	
	@Scheduled(fixedDelay = 30_000)
	public void handleSuspendedEvent () {
		log.info(String.format("SubscriberEventEndpoint.handleSuspendedEvent : Execution time %s", OffsetDateTime.now())) ;
		subscriberService.fetchFromSuspendedQueue()
		           .forEach(message -> {
		        	   log.info(String.format("Message received %s", message)) ;
		        	   
		        	   SubscriberDTO subscriberDTO = subscriberService.fetchSubscriber(message.getSubscriberId()) ;
		        	   
		        	   SubscriberSuspensionPayload payload = new SubscriberSuspensionPayload() ;
		        	   payload.setId(subscriberDTO.getId()) ;
	        		   payload.setFirstName(subscriberDTO.getFirstName()) ;
	        		   payload.setLastName(subscriberDTO.getLastName()) ;
	        		   payload.setLabel(subscriberDTO.getLabel()) ;
	        		   payload.setEmailAddress(subscriberDTO.getEmailAddress()) ;
	        		   log.info(String.format("Save reminder email : %s", payload)) ;
	        		   
	        		   ReminderEmailDTO reminderEmailDTO = null ;
	        		   try {
	        			   reminderEmailDTO = subscriberService.genReminderEmail(payload) ;
		        	   } catch (Exception e) {
		        		   log.error(String.format("Unable to generate reminder email : %s", payload), e) ;
		        		   return ;
		        	   }
	        		   
	        		   try {
	        			   reminderEmailService.sendReminderEmail(reminderEmailDTO) ;
		        	   } catch (Exception e) {
		        		   log.error(String.format("Unable to send reminder email : %s", reminderEmailDTO), e) ;
		        		   return ;
		        	   }
		           }) ;
	}
	
	
	
	@PreDestroy
	public void cleanUp() {
		
	}
}
