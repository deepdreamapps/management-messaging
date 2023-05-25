package tech.deepdreams.messaging.services;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.apiclient.SubscriberClient;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.dtos.SubscriberDTO;
import tech.deepdreams.messaging.requests.ReminderEmailPayload;
import tech.deepdreams.messaging.requests.SubscriberCreationPayload;
import tech.deepdreams.messaging.util.AmazonEmailSender;
import tech.deepdreams.subscriber.enums.SubscriberEventType;
import tech.deepdreams.subscriber.events.SubscriberCreatedEvent;


@Service
@Transactional
@Log4j2
public class SubscriberService {
	@Value("${webapp.baseUrl}")
	private String baseUrl ;
	
	@Value("${webapp.offerSelectionUrl}")
	private String offerSelectionUrl ;
	
	@Autowired
	private AmazonEmailSender emailSender ;
	
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
	
	
	public ReminderEmailDTO saveReminderEmail (SubscriberCreationPayload payload) throws IOException {
		Map<String, Object> templateModel = new HashMap<>() ;
		templateModel.put("firstName", payload.getFirstName()) ;
		templateModel.put("offerSelectionUrl", String.format("%s%s", baseUrl, offerSelectionUrl)) ;
		ReminderEmailPayload reminderEmail = ReminderEmailPayload.builder()
				.eventId(payload.getEventId())
				.eventType(SubscriberEventType.SUBSCRIBER_CREATED.name())
				.subject("Bienvenue / Welcome")
				.from("deepdream@deepdreams.tech")
				.to(payload.getEmailAddress())
				.templateModel(templateModel)
				.templateFile("subscriber/subscriberCreationEmail.html")
				.build() ;
		return emailSender.saveReminderEmail(reminderEmail) ;
	}
	
	
	
	public Optional<SubscriberDTO> fetchSubscriber (Long subscriberId) {
		return subscriberClient.fetchSubscriber(subscriberId) ;
	}
}
