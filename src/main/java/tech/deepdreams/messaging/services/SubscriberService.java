package tech.deepdreams.messaging.services;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.apiclient.SubscriberClient;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.dtos.SubscriberDTO;
import tech.deepdreams.messaging.mappers.ReminderEmailMapper;
import tech.deepdreams.messaging.models.ReminderEmail;
import tech.deepdreams.messaging.requests.ReminderEmailPayload;
import tech.deepdreams.messaging.requests.SubscriberSuspensionPayload;
import tech.deepdreams.messaging.util.AmazonEmailSender;
import tech.deepdreams.subscriber.events.SubscriberCreatedEvent;
import tech.deepdreams.subscriber.events.SubscriberSuspendedEvent;

@Log4j2
@Service
public class SubscriberService {
	@Value("${adminsys.baseUrl}")
	private String baseUrl;

	@Value("${adminsys.offerSelectionUrl}")
	private String offerSelectionUrl;

	@Autowired
	private SubscriberClient subscriberClient;

	@Autowired
	private AmazonEmailSender amazonEmailSender;

	@Autowired
	private ReminderEmailMapper reminderEmailMapper;

	
	
	public List<SubscriberCreatedEvent> fetchFromCreatedQueue() {
		try {
			return subscriberClient.fetchFromCreatedQueue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	public List<SubscriberSuspendedEvent> fetchFromSuspendedQueue() {
		try {
			return subscriberClient.fetchFromSuspendedQueue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	public ReminderEmailDTO genReminderEmail(SubscriberDTO subscriberDTO) throws IOException {
		Map<String, Object> templateModel = new HashMap<>();
		templateModel.put("firstName", subscriberDTO.getFirstName());
		
		String clearId = String.format("%d", subscriberDTO.getId()) ;

		Base32 base32Encoder = new Base32();
		String encodedId = base32Encoder.encodeToString(clearId.getBytes(StandardCharsets.UTF_8)) ;
		templateModel.put("offerSelectionUrl", String.format("%s%s/%s", baseUrl, offerSelectionUrl, encodedId)) ;

		// Decode data from Base32 (Java)
		// byte[] decodedBytes = base32Encoder.decode(encodedData);
		// String decodedData = new String(decodedBytes, StandardCharsets.UTF_8);
		// System.out.println("Decoded data: " + decodedData);

		// const decodedData = base32.decode(encodedData).toString('utf8'); (JavaScript)

		ReminderEmailPayload reminderEmailPayload = ReminderEmailPayload.builder()
				.subject("Bienvenue...")
				.from("no-reply@deepdreams.tech")
				.to(subscriberDTO.getEmailAddress())
				.templateModel(templateModel)
				.templateFile("subscriber/subscriberCreatedEmail.html").build();

		ReminderEmail reminderEmail = amazonEmailSender.genReminderEmail(reminderEmailPayload);

		log.info(String.format("Generated reminder email : %s", reminderEmail));

		return reminderEmailMapper.mapModelToDTO(reminderEmail);
	}

	
	
	public ReminderEmailDTO genReminderEmail(SubscriberSuspensionPayload suspensionPayload) throws IOException {
		Map<String, Object> templateModel = new HashMap<>();
		templateModel.put("firstName", suspensionPayload.getFirstName());

		ReminderEmailPayload reminderEmailPayload = ReminderEmailPayload.builder()
				.subject("Suspension de votre compte")
				.from("no-reply@deepdreams.tech")
				.to(suspensionPayload.getEmailAddress())
				.templateModel(templateModel)
				.templateFile("subscriber/subscriberSuspendedEmail.html").build();

		ReminderEmail reminderEmail = amazonEmailSender.genReminderEmail(reminderEmailPayload);

		log.info(String.format("Generated reminder email : %s", reminderEmail));

		return reminderEmailMapper.mapModelToDTO(reminderEmail);
	}

	
	public SubscriberDTO fetchSubscriber(Long id) {
		return subscriberClient.fetchSubscriber(id) ;
	}

}
