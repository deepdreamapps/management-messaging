package tech.deepdreams.messaging.services;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.mappers.ReminderEmailMapper;
import tech.deepdreams.messaging.models.ReminderEmail;
import tech.deepdreams.messaging.repositories.ReminderEmailRepository;
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
	private ReminderEmailRepository reminderEmailRepository ;
	
	@Autowired
	private ReminderEmailMapper reminderEmailMapper ;
	
	
	public ReminderEmailDTO saveReminderEmail (ReminderEmail reminderEmail) {
		ReminderEmail reminderEmailSaved = reminderEmailRepository.save(reminderEmail) ;
		
		log.info(String.format("Reminder email saved : %s", reminderEmailSaved)) ;
		
		return reminderEmailMapper.mapModelToDTO(reminderEmailSaved) ;
	}
	
	
	public ReminderEmailDTO saveReminderEmail (SubscriberCreationPayload subscriberCreationPayload) throws IOException {
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
		
		log.info(String.format("Save reminder email : %s", reminderEmail)) ;
		
		ReminderEmail reminderEmailSaved = reminderEmailRepository.save(reminderEmail) ;
		
		log.info(String.format("Reminder email saved : %s", reminderEmailSaved)) ;
		
		return reminderEmailMapper.mapModelToDTO(reminderEmailSaved) ;
	}
	
	
	public Optional<Boolean> existsByEventTypeAndEventId (String eventType, Long eventId){
		return reminderEmailRepository.existsByEventTypeAndEventId(eventType, eventId) ;
	}
	
	
	public void sendReminderEmail (ReminderEmailDTO reminderEmail){
		reminderEmailRepository.findById(reminderEmail.getId())
				     .ifPresent(email -> {
				    	 try {
							amazonEmailSender.sendReminderEmail(email) ;
							email.setSent(true) ;
					    	reminderEmailRepository.save(email) ;
						} catch (Exception e) {
							log.error(String.format("Mail not sent : %s", email), e) ;
						}
				     }) ;
	}
	

	public List<ReminderEmailDTO> fetchEmails(Boolean delivered) {
		return reminderEmailRepository.findBySent(delivered)
				        .stream()
				        .map(reminderEmail -> {
				        	return reminderEmailMapper.mapModelToDTO(reminderEmail) ;
				        })
				        .collect(Collectors.toList()) ;
	}
	
	
	public List<ReminderEmailDTO> fetchEmailsBetween(LocalDate startDate, LocalDate endDate) {
		OffsetDateTime startOffsetDateTime = OffsetDateTime.of(startDate, LocalTime.MIDNIGHT, ZoneOffset.UTC) ;
		OffsetDateTime endOffsetDateTime = OffsetDateTime.of(endDate, LocalTime.MAX, ZoneOffset.UTC) ;
		return reminderEmailRepository.findByInstantBetween(startOffsetDateTime, endOffsetDateTime)
				        .stream()
				        .map(reminderEmail -> {
				        	return reminderEmailMapper.mapModelToDTO(reminderEmail) ;
				        })
				        .collect(Collectors.toList()) ;
	}	
}
