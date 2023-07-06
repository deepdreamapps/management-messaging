package tech.deepdreams.messaging.util;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.models.ReminderEmail;
import tech.deepdreams.messaging.models.ReminderSms;
import tech.deepdreams.messaging.requests.ReminderEmailPayload;

@Log4j2
@AllArgsConstructor
@Service
public class TwilioSmsSender {
	private SpringTemplateEngine templateEngine ;
	 
	
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
            Message message = Message.creator(
            	        new PhoneNumber("+15558675309"),
            	        new PhoneNumber("+15017250604"),
            	        "This is the ship that made the Kessel Run in fourteen parsecs?"
            	      )
            	      .create();
            log.info(String.format("Email sent %s !", message.getSid()));
        } catch (Exception ex) {
        	log.error("The email was not sent.", ex);
        } 
	}
	
	
}
