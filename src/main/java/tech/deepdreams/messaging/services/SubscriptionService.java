package tech.deepdreams.messaging.services;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import tech.deepdreams.messaging.apiclient.SubscriptionClient;
import tech.deepdreams.messaging.dtos.SubscriptionDTO;
import tech.deepdreams.messaging.requests.SubscriptionCreationNotification;
import tech.deepdreams.messaging.util.Email;

@Service
@Transactional
@AllArgsConstructor
public class SubscriptionService {
	private SubscriptionClient subscriptionClient ;
	
	public void handleCreation (SubscriptionCreationNotification payload) throws IOException {
		Map<String, Object> templateModel = new HashMap<>() ;
		templateModel.put("firstName", payload.getFirstName()) ;
		templateModel.put("offerLabel", payload.getOfferLabel()) ;
		templateModel.put("applicationLabel", payload.getApplicationLabel()) ;
		templateModel.put("numberOfYears", payload.getNumberOfYears()) ;
		templateModel.put("expirationDate", payload.getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) ;
		templateModel.put("numberOfUsers", payload.getNumberOfUsers()) ;
		templateModel.put("numberOfUnits", payload.getNumberOfUnits()) ;
		templateModel.put("trialDuration", payload.getTrialDuration()) ;
		
		/*Email email = Email.builder()
				.subject("Votre souscription / Your subscription")
				.sender("deepdream@deepdreams.tech")
				.recipient(payload.getEmailAddress())
				.templateModel(templateModel)
				.template("subscription/subscriptionCreationEmail.html")
				.build() ;*/
		//emailSender.sendEmail(email) ;
	}
	
	
	public Optional<SubscriptionDTO> fetchSubscription (Long applicationId, Long subscriberId) {
		return subscriptionClient.fetchSubscription(applicationId, subscriberId) ;
	}
}
