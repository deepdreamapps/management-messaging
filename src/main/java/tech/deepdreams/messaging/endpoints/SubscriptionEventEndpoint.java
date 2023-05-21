package tech.deepdreams.messaging.endpoints;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.services.SubscriberService;
import tech.deepdreams.messaging.services.SubscriptionService;

@Service
@Log4j2
public class SubscriptionEventEndpoint { 
	private SubscriptionService  subscriptionService ;
	private SubscriberService subscriberService ;
	private ExecutorService executorService ;
	
	public SubscriptionEventEndpoint (SubscriptionService  subscriptionService, SubscriberService subscriberService) {
		this.subscriptionService = subscriptionService ;
		this.subscriberService = subscriberService ;
		executorService = Executors.newFixedThreadPool(2) ;
	}
	
	
	/*@SqsListener(value = "", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void handleEvent (final SubscriptionCreatedEvent event) {
		executorService.submit(() -> {
			
			Mono.zip(subscriptionService.fetchSubscription(event.getApplicationId(), event.getSubscriberId()), 
					subscriberService.fetchSubscriber(event.getSubscriberId()))
				.map(tuple2 -> {
					SubscriptionDTO subscriptionDTO = tuple2.getT1() ;
					SubscriberDTO subscriberDTO = tuple2.getT2() ;
					log.info(String.format("Publishing subscriptionDTO subscriberDTO %s %s", subscriptionDTO, subscriberDTO)) ;
					SubscriptionCreationNotification payload = SubscriptionCreationNotification.builder()
							.firstName(subscriberDTO.getFirstName())
							.lastName(subscriberDTO.getLastName())
							.label(subscriberDTO.getLabel())
							.applicationLabel(subscriptionDTO.getApplicationLabel())
							.offerLabel(subscriptionDTO.getOfferLabel())
							.trialDuration(subscriptionDTO.getTrialDuration())							
							.expirationDate(subscriptionDTO.getStartDate().plusDays(subscriptionDTO.getTrialDuration()))
							.emailAddress(subscriberDTO.getEmailAddress())
							.numberOfRequests(subscriptionDTO.getNumberOfRequests())
							.numberOfUnits(subscriptionDTO.getNumberOfUnits())
							.numberOfUsers(subscriptionDTO.getNumberOfUsers())
							.numberOfYears(subscriptionDTO.getNumberOfYears())
							.build() ;
					return payload ;
				}).subscribe(payload -> {
					log.info(String.format("Publishing payload %s", payload)) ;
					subscriptionService.handleCreation(payload) ;
				}) ;
			
		}) ;
	}*/
}
