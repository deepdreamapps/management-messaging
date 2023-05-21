package tech.deepdreams.messaging.apiclient;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.dtos.SubscriptionDTO;

@Service
@Log4j2
public class SubscriptionClient {
	@Value("${subscription.fetchByAppAndSubscriberUrl}")
	private String fetchByAppAndSubscriberUrl ;
	
	@Autowired
	private RestTemplate restTemplate ;
	
	 
	 
	public Optional<SubscriptionDTO> fetchSubscription(Long applicationId, Long subscriberId) {
		log.info(String.format("Calling Subscription API to get subscription with applicationId %d and subscriberId %d", 
				applicationId, subscriberId)) ;
		
		SubscriptionDTO subscription = restTemplate.getForObject(fetchByAppAndSubscriberUrl, 
				SubscriptionDTO.class, applicationId, subscriberId) ;
		
		return Optional.ofNullable(subscription) ;
    }
}
