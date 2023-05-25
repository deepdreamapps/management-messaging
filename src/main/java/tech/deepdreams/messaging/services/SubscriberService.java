package tech.deepdreams.messaging.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.apiclient.SubscriberClient;
import tech.deepdreams.messaging.dtos.SubscriberDTO;
import tech.deepdreams.subscriber.events.SubscriberCreatedEvent;

@Log4j2
@Service
public class SubscriberService {
	@Autowired
	private SubscriberClient subscriberClient ;
	
	
	public List<SubscriberCreatedEvent> fetchMessagesFromQueue(){
		try {
			return subscriberClient.fetchMessagesFromQueue() ;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>() ;
		} 
	}

	
	public Optional<SubscriberDTO> fetchSubscriber (Long subscriberId) {
		return subscriberClient.fetchSubscriber(subscriberId) ;
	}
}
