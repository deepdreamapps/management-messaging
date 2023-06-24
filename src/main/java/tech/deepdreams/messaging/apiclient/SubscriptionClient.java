package tech.deepdreams.messaging.apiclient;
import java.util.ArrayList;
import java.util.List;
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
import tech.deepdreams.messaging.dtos.SubscriptionDTO;
import tech.deepdreams.subscription.events.SubscriptionCreatedEvent;

@Log4j2
@Service
public class SubscriptionClient {
	@Value("${subscription.queueSubscriptionCreatedUrl}")
	private String queueSubscriptionCreatedUrl ;
	
	@Value("${subscription.fetchByAppAndSubscriberUrl}")
	private String fetchByAppAndSubscriberUrl ;
	
	@Autowired
	private RestTemplate restTemplate ;
	
	@Autowired
	private AmazonSQSClient amazonSQSClient ;
	
	@Autowired
	private ObjectMapper objectMapper ;
	
	
	public List<SubscriptionCreatedEvent> fetchFromCreatedQueue() throws JsonMappingException, JsonProcessingException{
		List<SubscriptionCreatedEvent> listOfEvents = new ArrayList<>() ;
		ReceiveMessageResult result = amazonSQSClient.receiveMessage(queueSubscriptionCreatedUrl) ;
		for(Message message : result.getMessages()) {
			SNSMessage SNSMessage = objectMapper.readValue(message.getBody(), SNSMessage.class) ;
			SubscriptionCreatedEvent event = objectMapper.readValue(SNSMessage.getMessage(), SubscriptionCreatedEvent.class) ;
			listOfEvents.add(event) ;
			log.info(String.format("Message retrieved from the queue %s", message)) ;
			amazonSQSClient.deleteMessage(queueSubscriptionCreatedUrl, message.getReceiptHandle()) ;
		}
		
		log.info(String.format("Number of messages retrieved from the queue %s", listOfEvents.size())) ;
        return listOfEvents ;
	}
	
	 
	 
	public SubscriptionDTO fetchSubscription(Long applicationId, Long subscriberId) {
		log.info(String.format("Calling Subscription API to get subscription with applicationId %d and subscriberId %d", applicationId, subscriberId)) ;
		SubscriptionDTO subscriptionDTO = restTemplate.getForObject(fetchByAppAndSubscriberUrl, SubscriptionDTO.class, applicationId, subscriberId) ;
		return subscriptionDTO ;
    }
}
