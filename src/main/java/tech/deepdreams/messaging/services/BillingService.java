package tech.deepdreams.messaging.services;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.billing.enums.BillEventType;
import tech.deepdreams.billing.events.BillCreatedEvent;
import tech.deepdreams.messaging.apiclient.BillingClient;
import tech.deepdreams.messaging.dtos.ApplicationDTO;
import tech.deepdreams.messaging.dtos.BillDTO;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.dtos.SubscriberDTO;
import tech.deepdreams.messaging.dtos.SubscriptionDTO;
import tech.deepdreams.messaging.mappers.ReminderEmailMapper;
import tech.deepdreams.messaging.models.ReminderEmail;
import tech.deepdreams.messaging.requests.ReminderEmailPayload;
import tech.deepdreams.messaging.util.AmazonEmailSender;

@Log4j2
@Service
public class BillingService {
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
	
	
	public List<BillCreatedEvent> fetchFromExpiredQueue() {
		try {
			return billingClient.fetchFromCreatedQueue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	

	public ReminderEmailDTO genBillCreatedReminderEmail(BillDTO bill) throws IOException {
		SubscriberDTO subscriber = bill.getSubscriber() ;
		SubscriptionDTO subscription = bill.getSubscription() ;
		ApplicationDTO application = subscription.getApplication() ;
		
		Map<String, Object> templateModel = new HashMap<>();
		templateModel.put("firstName", subscriber.getFirstName());
		templateModel.put("applicationLabel", application.getLabel()) ;
		templateModel.put("billId", bill.getId()) ;
		
		String invoiceObject = switch(bill.getBillType()) {
			case "SUBSCRIPTION" :
				yield String.format("Abonnement à l'application %s ", application.getLabel()) ;  
			case "CAPACITY_INCREASE" :
				yield String.format("Augmentation de capacités (%d unité(s) et %d utilisateur(s)) au sein de l'application %s",
						subscription.getRequestedUnits(), subscription.getRequestedUsers(), application.getLabel()) ;
			case "ADVANCED_SECURITY" :
				yield "Abonnement à l'option sécurité avancée " ;
			default :
				yield "" ;
		} ;
		templateModel.put("invoiceObject", invoiceObject) ;
		templateModel.put("amount", bill.getAmountWithTaxes()) ;
		templateModel.put("dueDate", bill.getPaymentDelay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) ;
		
		ReminderEmailPayload reminderEmailPayload = ReminderEmailPayload.builder()
				.eventType(BillEventType.BILL_CREATED.name())
				.subject("Nouvelle facture disponible")
				.from("no-reply@deepdreams.tech").to(subscriber.getEmailAddress())
				.templateModel(templateModel)
				.templateFile("billing/billCreationEmail.html")
				.build() ;

		ReminderEmail reminderEmail = amazonEmailSender.genReminderEmail(reminderEmailPayload);

		log.info(String.format("Generated reminder email : %s", reminderEmail));

		return reminderEmailMapper.mapModelToDTO(reminderEmail);
	}

	
	
	public ReminderEmailDTO genBillExpiredReminderEmail(BillDTO bill) throws IOException {
		SubscriberDTO subscriber = bill.getSubscriber() ;
		SubscriptionDTO subscription = bill.getSubscription() ;
		ApplicationDTO application = subscription.getApplication() ;
		
		Map<String, Object> templateModel = new HashMap<>();
		templateModel.put("firstName", subscriber.getFirstName());
		templateModel.put("applicationLabel", application.getLabel()) ;
		templateModel.put("billId", bill.getId()) ;
		
		String invoiceObject = switch(bill.getBillType()) {
			case "SUBSCRIPTION" :
				yield String.format("Abonnement à l'application %s ", application.getLabel()) ;  
			case "CAPACITY_INCREASE" :
				yield String.format("Augmentation de capacités (%d unité(s) et %d utilisateur(s)) au sein de l'application %s",
						subscription.getRequestedUnits(), subscription.getRequestedUsers(), application.getLabel()) ;
			case "ADVANCED_SECURITY" :
				yield "Abonnement à l'option sécurité avancée " ;
			default :
				yield "" ;
		} ;
		templateModel.put("invoiceObject", invoiceObject) ;
		templateModel.put("amount", bill.getAmountWithTaxes()) ;
		templateModel.put("dueDate", bill.getPaymentDelay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) ;
		
		ReminderEmailPayload reminderEmailPayload = ReminderEmailPayload.builder()
				.eventType(BillEventType.BILL_EXPIRED.name())
				.subject(String.format("Échéance de paiement dépassée pour votre abonnement à %s", application.getLabel()))
				.from("no-reply@deepdreams.tech")
				.to(subscriber.getEmailAddress())
				.templateModel(templateModel)
				.templateFile("billing/billExpirationEmail.html")
				.build() ;

		ReminderEmail reminderEmail = amazonEmailSender.genReminderEmail(reminderEmailPayload);

		log.info(String.format("Generated reminder email : %s", reminderEmail));

		return reminderEmailMapper.mapModelToDTO(reminderEmail);
	}

	
	public BillDTO fetchBill(Long id) {
		return billingClient.fetchBill(id) ;
	}

}
