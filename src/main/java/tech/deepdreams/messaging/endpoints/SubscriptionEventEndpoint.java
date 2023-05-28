package tech.deepdreams.messaging.endpoints;
import java.time.OffsetDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.services.SubscriptionService;


@Log4j2
@AllArgsConstructor
@Service
public class SubscriptionEventEndpoint { 
	private SubscriptionService  subscriptionService ;
	
	
	
	@Scheduled(fixedDelay = 30_000)
	public void handleCreatedEvent () {
		log.info(String.format("SubscriptionEventEndpoint.handleCreatedEvent : Execution time %s", OffsetDateTime.now())) ;
		subscriptionService.fetchFromCreatedQueue()
		           .forEach(message -> {
		        	   log.info(String.format("Message received %s", message)) ;

		           }) ;
	}
}
