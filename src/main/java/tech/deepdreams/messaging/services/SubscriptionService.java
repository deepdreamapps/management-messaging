package tech.deepdreams.messaging.services;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.apiclient.SecretsManagerClient;
import tech.deepdreams.messaging.apiclient.SubscriptionClient;
import tech.deepdreams.messaging.dtos.ApplicationDTO;
import tech.deepdreams.messaging.dtos.OfferDTO;
import tech.deepdreams.messaging.dtos.OfferPricingDTO;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.dtos.SubscriberDTO;
import tech.deepdreams.messaging.dtos.SubscriptionDTO;
import tech.deepdreams.messaging.mappers.ReminderEmailMapper;
import tech.deepdreams.messaging.models.ReminderEmail;
import tech.deepdreams.messaging.requests.ReminderEmailPayload;
import tech.deepdreams.messaging.util.AmazonEmailSender;
import tech.deepdreams.subscription.events.SubscriptionActivatedEvent;
import tech.deepdreams.subscription.events.SubscriptionCreatedEvent;
import tech.deepdreams.subscription.events.SubscriptionSuspendedEvent;

@Log4j2
@AllArgsConstructor
@Service
public class SubscriptionService {
	@Autowired
	private SubscriptionClient subscriptionClient ;
	
	@Autowired
	private AmazonEmailSender amazonEmailSender;

	@Autowired
	private ReminderEmailMapper reminderEmailMapper;
	
	@Autowired
	private SecretsManagerClient secretsManagerClient ;
	
	
	public List<SubscriptionCreatedEvent> fetchFromCreatedQueue() {
		try {
			return subscriptionClient.fetchFromCreatedQueue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}
	
	
	public List<SubscriptionActivatedEvent> fetchFromActivatedQueue() {
		try {
			return subscriptionClient.fetchFromActivatedQueue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}
	
	
	public List<SubscriptionSuspendedEvent> fetchFromSuspendedQueue() {
		try {
			return subscriptionClient.fetchFromSuspendedQueue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}
	
	
	public ReminderEmailDTO genSubscriptionCreatedReminderEmail(SubscriptionDTO subscription) throws IOException {
		SubscriberDTO subscriber = subscription.getSubscriber() ;
		ApplicationDTO application = subscription.getApplication() ;
		OfferDTO offer = subscription.getOffer() ;
		OfferPricingDTO offerPricing = subscription.getOfferPricing() ;
		
		Map<String, Object> templateModel = new HashMap<>();
		templateModel.put("firstName", subscriber.getFirstName());
		templateModel.put("emailAddress", subscriber.getEmailAddress());
		templateModel.put("defaultPassword", secretsManagerClient.getDefaultPassword());
		templateModel.put("applicationLabel", application.getLabel());
		templateModel.put("offerLabel", offer.getLabel());
		templateModel.put("startDate", subscription.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		templateModel.put("numberOfUsers", offer.getMaxUsers());
		templateModel.put("numberOfUnits", offer.getMaxUnits());
		templateModel.put("trialDuration", offer.getTrialDuration());
		String paymentFrequency = switch(offerPricing.getPaymentFrequency()) {
			case "MONTHLY"  :
				yield "Mensuelle" ;
			case "QUATERLY"  :
				yield "Trimestrielle" ;
			case "BIANNUALY"  :
				yield "Semestrielle" ;
			case "ANNUALY"  :
				yield "Annuelle" ;
			default :
				yield "" ;
 		} ;
		templateModel.put("paymentFrequency", paymentFrequency);
		
		String templateFile = String.format("subscription/%d/subscriptionCreationEmail.html", application.getId()) ;
		
		ReminderEmailPayload reminderEmailPayload = ReminderEmailPayload.builder()
				.subject(String.format("Votre abonnement à %s", application.getLabel()))
				.from("no-reply@deepdreams.tech")
				.to(subscriber.getEmailAddress())
				.templateModel(templateModel)
				.templateFile(templateFile).build();

		ReminderEmail reminderEmail = amazonEmailSender.genReminderEmail(reminderEmailPayload);

		log.info(String.format("Generated reminder email : %s", reminderEmail));

		return reminderEmailMapper.mapModelToDTO(reminderEmail);
	}
	
	
	public ReminderEmailDTO genSubscriptionActivatedReminderEmail(SubscriptionDTO subscription) throws IOException {
		SubscriberDTO subscriber = subscription.getSubscriber() ;
		ApplicationDTO application = subscription.getApplication() ;
		
		Map<String, Object> templateModel = new HashMap<>();
		templateModel.put("firstName", subscriber.getFirstName());
		templateModel.put("emailAddress", subscriber.getEmailAddress());
		templateModel.put("applicationLabel", application.getLabel());
		
		String templateFile = String.format("subscription/%d/subscriptionActivationEmail.html", application.getId()) ;
		
		ReminderEmailPayload reminderEmailPayload = ReminderEmailPayload.builder()
				.subject(String.format("Activation de l'abonnement à %s", application.getLabel()))
				.from("no-reply@deepdreams.tech")
				.to(subscriber.getEmailAddress())
				.templateModel(templateModel)
				.templateFile(templateFile).build();

		ReminderEmail reminderEmail = amazonEmailSender.genReminderEmail(reminderEmailPayload);

		log.info(String.format("Generated reminder email : %s", reminderEmail));

		return reminderEmailMapper.mapModelToDTO(reminderEmail);
	}
	
	
	public ReminderEmailDTO genSubscriptionSuspendedReminderEmail(SubscriptionDTO subscription) throws IOException {
		SubscriberDTO subscriber = subscription.getSubscriber() ;
		ApplicationDTO application = subscription.getApplication() ;
		
		Map<String, Object> templateModel = new HashMap<>();
		templateModel.put("firstName", subscriber.getFirstName());
		templateModel.put("emailAddress", subscriber.getEmailAddress());
		templateModel.put("applicationLabel", application.getLabel());

		String templateFile = String.format("subscription/%d/subscriptionSuspensionEmail.html", application.getId()) ;
		
		ReminderEmailPayload reminderEmailPayload = ReminderEmailPayload.builder()
				.subject(String.format("Votre abonnement à %s", application.getLabel()))
				.from("no-reply@deepdreams.tech")
				.to(subscriber.getEmailAddress())
				.templateModel(templateModel)
				.templateFile(templateFile).build();

		ReminderEmail reminderEmail = amazonEmailSender.genReminderEmail(reminderEmailPayload);

		log.info(String.format("Generated reminder email : %s", reminderEmail));

		return reminderEmailMapper.mapModelToDTO(reminderEmail);
	}
	
	
	public SubscriptionDTO fetchSubscription (Long applicationId, Long subscriberId) {
		return subscriptionClient.fetchSubscription(applicationId, subscriberId) ;
	}
	
	public SubscriptionDTO fetchSubscription (Long subscriptionId) {
		return subscriptionClient.fetchSubscription(subscriptionId) ;
	}
}
