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
import tech.deepdreams.subscription.events.SubscriptionActivatedEvent;
import tech.deepdreams.subscription.events.SubscriptionCreatedEvent;
import tech.deepdreams.subscription.events.SubscriptionSuspendedEvent;

@Log4j2
@Service
public class SubscriptionClient {
	@Value("${subscription.queueSubscriptionCreatedUrl}")
	private String queueSubscriptionCreatedUrl ;
	
	@Value("${subscription.queueSubscriptionActivatedUrl}")
	private String queueSubscriptionActivatedUrl ;
	
	@Value("${subscription.queueSubscriptionSuspendedUrl}")
	private String queueSubscriptionSuspendedUrl ;
	
	@Value("${subscription.fetchByAppAndSubscriberUrl}")
	private String fetchByAppAndSubscriberUrl ;
	
	@Value("${subscription.fetchSubscriptionByIdUrl}")
	private String fetchSubscriptionByIdUrl ;
	
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
	
	
	
	public List<SubscriptionActivatedEvent> fetchFromActivatedQueue() throws JsonMappingException, JsonProcessingException{
		List<SubscriptionActivatedEvent> listOfEvents = new ArrayList<>() ;
		ReceiveMessageResult result = amazonSQSClient.receiveMessage(queueSubscriptionActivatedUrl) ;
		for(Message message : result.getMessages()) {
			SNSMessage SNSMessage = objectMapper.readValue(message.getBody(), SNSMessage.class) ;
			SubscriptionActivatedEvent event = objectMapper.readValue(SNSMessage.getMessage(), SubscriptionActivatedEvent.class) ;
			listOfEvents.add(event) ;
			log.info(String.format("Message retrieved from the queue %s", message)) ;
			amazonSQSClient.deleteMessage(queueSubscriptionActivatedUrl, message.getReceiptHandle()) ;
		}
		
		log.info(String.format("Number of messages retrieved from the queue %s", listOfEvents.size())) ;
        return listOfEvents ;
	}
	
	
	public List<SubscriptionSuspendedEvent> fetchFromSuspendedQueue() throws JsonMappingException, JsonProcessingException{
		List<SubscriptionSuspendedEvent> listOfEvents = new ArrayList<>() ;
		ReceiveMessageResult result = amazonSQSClient.receiveMessage(queueSubscriptionSuspendedUrl) ;
		for(Message message : result.getMessages()) {
			SNSMessage SNSMessage = objectMapper.readValue(message.getBody(), SNSMessage.class) ;
			SubscriptionSuspendedEvent event = objectMapper.readValue(SNSMessage.getMessage(), SubscriptionSuspendedEvent.class) ;
			listOfEvents.add(event) ;
			log.info(String.format("Message retrieved from the queue %s", message)) ;
			amazonSQSClient.deleteMessage(queueSubscriptionSuspendedUrl, message.getReceiptHandle()) ;
		}
		
		log.info(String.format("Number of messages retrieved from the queue %s", listOfEvents.size())) ;
        return listOfEvents ;
	}
	
	 
	 
	public SubscriptionDTO fetchSubscription(Long applicationId, Long subscriberId) {
		log.info(String.format("Calling Subscription API to get subscription with applicationId %d and subscriberId %d", applicationId, subscriberId)) ;
		SubscriptionDTO subscriptionDTO = restTemplate.getForObject(fetchByAppAndSubscriberUrl, SubscriptionDTO.class, applicationId, subscriberId) ;
		return subscriptionDTO ;
    }
	
	public SubscriptionDTO fetchSubscription(Long subscriptionId) {
		log.info(String.format("Calling Subscription API to get subscription with subscriptionId %d", subscriptionId)) ;
		SubscriptionDTO subscriptionDTO = restTemplate.getForObject(fetchSubscriptionByIdUrl, SubscriptionDTO.class, subscriptionId) ;
		return subscriptionDTO ;
    }
}
