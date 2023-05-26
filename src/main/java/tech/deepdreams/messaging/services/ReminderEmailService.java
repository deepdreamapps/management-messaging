package tech.deepdreams.messaging.services;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.mappers.ReminderEmailMapper;
import tech.deepdreams.messaging.models.ReminderEmail;
import tech.deepdreams.messaging.requests.ReminderEmailPayload;
import tech.deepdreams.messaging.requests.SubscriberCreationPayload;
import tech.deepdreams.messaging.util.AmazonEmailSender;
import tech.deepdreams.subscriber.enums.SubscriberEventType;

@Log4j2
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ReminderEmailService {
	@Value("${webapp.baseUrl}")
	private String baseUrl ;
	
	@Value("${webapp.offerSelectionUrl}")
	private String offerSelectionUrl ;
	
	@Autowired
	private AmazonEmailSender amazonEmailSender ;
	
	@Autowired
	private ReminderEmailMapper reminderEmailMapper ;
	
	
	
	public ReminderEmailDTO genReminderEmail (SubscriberCreationPayload subscriberCreationPayload) throws IOException {
		Map<String, Object> templateModel = new HashMap<>() ;
		templateModel.put("firstName", subscriberCreationPayload.getFirstName()) ;
		templateModel.put("offerSelectionUrl", String.format("%s%s", baseUrl, offerSelectionUrl)) ;
		
		ReminderEmailPayload reminderEmailPayload = ReminderEmailPayload.builder()
				.eventId(subscriberCreationPayload.getEventId())
				.eventType(SubscriberEventType.SUBSCRIBER_CREATED.name())
				.subject("Bienvenue / Welcome")
				.from("no-reply.deepdreams.tech")
				.to(subscriberCreationPayload.getEmailAddress())
				.templateModel(templateModel)
				.templateFile("subscriber/subscriberCreationEmail.html")
				.build() ;
		
		ReminderEmail reminderEmail = amazonEmailSender.genReminderEmail(reminderEmailPayload) ;
		
		log.info(String.format("Generated reminder email : %s", reminderEmail)) ;

		return reminderEmailMapper.mapModelToDTO(reminderEmail) ;
	}
	

	public void sendReminderEmail (ReminderEmailDTO reminderEmailDTO){
		try {
			amazonEmailSender.sendReminderEmail(reminderEmailMapper.mapDTOToModel(reminderEmailDTO)) ;
		} catch (IOException | MessagingException e) {
			log.info(String.format("Reminder email not sent: %s", reminderEmailDTO)) ;
		}
	}
	
}
