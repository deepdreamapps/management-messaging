package tech.deepdreams.messaging.services;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.apiclient.SubscriptionClient;
import tech.deepdreams.messaging.dtos.SubscriptionDTO;
import tech.deepdreams.subscription.events.SubscriptionCreatedEvent;

@Log4j2
@AllArgsConstructor
@Service
public class SubscriptionService {
	private SubscriptionClient subscriptionClient ;
	
	
	public List<SubscriptionCreatedEvent> fetchFromCreatedQueue() {
		try {
			return subscriptionClient.fetchFromCreatedQueue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}
	
	
	public SubscriptionDTO fetchSubscription (Long applicationId, Long subscriberId) {
		return subscriptionClient.fetchSubscription(applicationId, subscriberId) ;
	}
}
