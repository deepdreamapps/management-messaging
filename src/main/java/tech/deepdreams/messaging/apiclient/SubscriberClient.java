package tech.deepdreams.messaging.apiclient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.dtos.SubscriberDTO;
import tech.deepdreams.subscriber.events.SubscriberCreatedEvent;
import tech.deepdreams.subscriber.events.SubscriberSuspendedEvent;


@Log4j2
@Service
public class SubscriberClient {
	@Value("${messaging.queue_subscriber_created_url}")
	private String queueSubscriberCreatedUrl ;
	
	@Value("${messaging.queue_subscriber_suspended_url}")
	private String queueSubscriberSuspendedUrl ;
	
	@Value("${subscriber.fetchSubscriberByIdUrl}")
	private String fetchSubscriberByIdUrl ;
	
	@Autowired
	private RestTemplate restTemplate ;
	
	@Autowired
	private AmazonSQSClient amazonSQSClient ;
	
	@Autowired
	private ObjectMapper objectMapper ;
	

	public List<SubscriberCreatedEvent> fetchFromCreatedQueue() throws JsonMappingException, JsonProcessingException{
		List<SubscriberCreatedEvent> listOfEvents = new ArrayList<>() ;
		ReceiveMessageResult result = amazonSQSClient.receiveMessage(queueSubscriberCreatedUrl) ;
		for(Message message : result.getMessages()) {
			amazonSQSClient.deleteMessage(queueSubscriberCreatedUrl, message.getReceiptHandle()) ;
			SubscriberCreatedEvent event = objectMapper.readValue(message.getBody(), SubscriberCreatedEvent.class) ;
			listOfEvents.add(event) ;
			log.info(String.format("Message retrieved from the queue %s", message)) ;
		}
		log.info(String.format("Number of messages retrieved from the queue %s", listOfEvents.size())) ;
        return listOfEvents ;
	}
	
	
	public List<SubscriberSuspendedEvent> fetchFromSuspendedQueue() throws JsonMappingException, JsonProcessingException{
		List<SubscriberSuspendedEvent> listOfEvents = new ArrayList<>() ;
		ReceiveMessageResult result = amazonSQSClient.receiveMessage(queueSubscriberSuspendedUrl) ;
		for(Message message : result.getMessages()) {
			amazonSQSClient.deleteMessage(queueSubscriberCreatedUrl, message.getReceiptHandle()) ;
			SubscriberSuspendedEvent event = objectMapper.readValue(message.getBody(), SubscriberSuspendedEvent.class) ;
			listOfEvents.add(event) ;
			log.info(String.format("Message retrieved from the queue %s", message)) ;
		}
		log.info(String.format("Number of messages retrieved from the queue %s", listOfEvents.size())) ;
        return listOfEvents ;
	}
	
	 
	public SubscriberDTO fetchSubscriber(Long subscriberId) {
		log.info(String.format("Calling Subscriber API to get subscriber with subscriberId %d", 
				subscriberId)) ;
		
		SubscriberDTO subscriber = restTemplate.getForObject(fetchSubscriberByIdUrl, 
				SubscriberDTO.class, subscriberId) ;
		
		return subscriber ;
    }
}
