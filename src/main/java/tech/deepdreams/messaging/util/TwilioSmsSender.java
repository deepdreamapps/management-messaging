package tech.deepdreams.messaging.util;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.models.ReminderEmail;
import tech.deepdreams.messaging.models.ReminderSms;
import tech.deepdreams.messaging.models.VirtualPhoneNumber;
import tech.deepdreams.messaging.repositories.PhoneNumberRepository;
import tech.deepdreams.messaging.repositories.VirtualPhoneNumberRepository;
import tech.deepdreams.messaging.requests.ReminderEmailPayload;

@Log4j2
@AllArgsConstructor
@Service
public class TwilioSmsSender {
	private SpringTemplateEngine templateEngine ;
	private VirtualPhoneNumberRepository phoneNumberRepository ;
	private static final String ACCOUNT_SID = "ACb3515f71498f6642ad45efa422e4a1ab";
	private static final String AUTH_TOKEN = "d6280299877c0da00a0d59d9b2a62d3c";
	 
	
	public ReminderEmail genReminderEmail(ReminderEmailPayload payload) throws IOException {

		final Context context = new Context();
		
		payload.getTemplateModel().forEach((paramName, paramValue) -> {
			context.setVariable(paramName, paramValue) ;
		}) ;
		
		final String htmlBody = templateEngine.process(payload.getTemplateFile(), context) ;
		
		return ReminderEmail.builder()
					.subject(payload.getSubject())
					.recipient(payload.getTo())
					.content(htmlBody)
					.build() ;
	}
	
	
	public void sendReminderSms(ReminderSms reminderSms)  {
        try{
            log.info("Sending...") ;
            VirtualPhoneNumber phoneNumber = phoneNumberRepository.findByCountryId(reminderSms.getCountryId()).get(0) ;
            Twilio.init(phoneNumber.getAccountSid(), phoneNumber.getAuthToken()) ;
            Message message = Message.creator(
            	        new PhoneNumber(reminderSms.getRecipient()),
            	        new PhoneNumber(phoneNumber.getTwilioNumber()),
            	        reminderSms.getContent()
            	      )
            	      .create() ;
            log.info(String.format("Email sent %s !", message.getSid()));
        } catch (Exception ex) {
        	log.error("The email was not sent.", ex);
        } 
	}
	
	
}
