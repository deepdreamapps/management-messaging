package tech.deepdreams.messaging.services;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.billing.enums.BillEventType;
import tech.deepdreams.billing.events.BillCreatedEvent;
import tech.deepdreams.messaging.apiclient.BillingClient;
import tech.deepdreams.messaging.dtos.BillDTO;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.dtos.SubscriberDTO;
import tech.deepdreams.messaging.dtos.SubscriptionDTO;
import tech.deepdreams.messaging.mappers.ReminderEmailMapper;
import tech.deepdreams.messaging.models.ReminderEmail;
import tech.deepdreams.messaging.requests.ReminderEmailPayload;
import tech.deepdreams.messaging.requests.SubscriberSuspensionPayload;
import tech.deepdreams.messaging.util.AmazonEmailSender;
import tech.deepdreams.subscriber.enums.SubscriberEventType;

@Log4j2
@Service
public class BillService {
	@Autowired
	private BillingClient billingClient;

	@Autowired
	private AmazonEmailSender amazonEmailSender;

	@Autowired
	private ReminderEmailMapper reminderEmailMapper;

	
	
	public List<BillCreatedEvent> fetchFromCreatedQueue() {
		try {
			return billingClient.fetchFromCreatedQueue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	

	public ReminderEmailDTO genReminderEmail(BillDTO bill) throws IOException {
		SubscriberDTO subscriber = bill.getSubscriber() ;
		SubscriptionDTO subscription = bill.getSubscription() ;
		
		Map<String, Object> templateModel = new HashMap<>();
		templateModel.put("firstName", subscriber.getFirstName());
		
		ReminderEmailPayload reminderEmailPayload = ReminderEmailPayload.builder()
				.eventType(BillEventType.BILL_CREATED.name()).subject(" Nouvelle facture disponible")
				.from("no-reply@deepdreams.tech").to(subscriber.getEmailAddress()).templateModel(templateModel)
				.templateFile("billing/billCreationEmail.html").build();

		ReminderEmail reminderEmail = amazonEmailSender.genReminderEmail(reminderEmailPayload);

		log.info(String.format("Generated reminder email : %s", reminderEmail));

		return reminderEmailMapper.mapModelToDTO(reminderEmail);
	}


	public BillDTO fetchBill(Long id) {
		return billingClient.fetchBill(id) ;
	}

}
