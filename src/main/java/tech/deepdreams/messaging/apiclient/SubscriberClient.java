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


@Log4j2
@Service
public class SubscriberClient {
	@Value("${messaging.queue_subscriber_created_url}")
	private String queueSubscriberCreatedUrl ;
	
	@Value("${subscriber.fetchSubscriberByIdUrl}")
	private String fetchSubscriberByIdUrl ;
	
	@Autowired
	private RestTemplate restTemplate ;
	
	@Autowired
	private AmazonSQSClient amazonSQSClient ;
	
	@Autowired
	private ObjectMapper objectMapper ;
	

	public List<SubscriberCreatedEvent> fetchMessagesFromQueue() throws JsonMappingException, JsonProcessingException{
		List<SubscriberCreatedEvent> eventsList = new ArrayList<>() ;
		ReceiveMessageResult result = amazonSQSClient.receiveMessage(queueSubscriberCreatedUrl) ;
		for(Message message : result.getMessages()) {
			SubscriberCreatedEvent event = objectMapper.readValue(message.getBody(), SubscriberCreatedEvent.class) ;
			eventsList.add(event) ;
			amazonSQSClient.deleteMessage(queueSubscriberCreatedUrl, message.getReceiptHandle()) ;
		}
		log.info(String.format("Number of messages retrieved from the queue %s", eventsList.size())) ;
        return eventsList ;
	}
	
	 
	public Optional<SubscriberDTO> fetchSubscriber(Long subscriberId) {
		log.info(String.format("Calling Subscriber API to get subscriber with subscriberId %d", 
				subscriberId)) ;
		
		SubscriberDTO subscriber = restTemplate.getForObject(fetchSubscriberByIdUrl, 
				SubscriberDTO.class, subscriberId) ;
		
		return Optional.ofNullable(subscriber) ;
    }
}
