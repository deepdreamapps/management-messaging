package tech.deepdreams.messaging.util;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.models.ReminderEmail;
import tech.deepdreams.messaging.requests.ReminderEmailPayload;

@Log4j2
@AllArgsConstructor
@Service
public class AmazonEmailSender {
	private SpringTemplateEngine templateEngine ;
    private AmazonSimpleEmailService amazonSEService ;
	 
	
	public ReminderEmail genReminderEmail(ReminderEmailPayload payload) throws IOException {

		final Context context = new Context();
		
		payload.getTemplateModel().forEach((paramName, paramValue) -> {
			context.setVariable(paramName, paramValue) ;
		}) ;
		
		final String htmlBody = templateEngine.process(payload.getTemplateFile(), context) ;
		
		return ReminderEmail.builder()
					.eventType(payload.getEventType())
					.eventId(payload.getEventId())
					.instant(OffsetDateTime.now())
					.subject(payload.getSubject())
					.sender(payload.getFrom())
					.recipient(payload.getTo())
					.content(htmlBody)
					.sent(false)
					.build() ;
	}
	
	
	public void sendReminderEmail(ReminderEmail reminderEmail) throws IOException, MessagingException {
		String from = reminderEmail.getSender() ;
		String to  = reminderEmail.getRecipient() ;
		String subject = reminderEmail.getSubject() ;		
		String htmlBody = reminderEmail.getContent() ;
		
		Properties props = System.getProperties();
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.port", 587); 
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.auth", "true");
    	
    	Session session = Session.getDefaultInstance(props);
    	
    	MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from, to)) ;
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        
        msg.setContent(htmlBody, "text/html") ;
        // msg.setHeader("X-SES-CONFIGURATION-SET", "ConfigSet") ;
        Transport transport = session.getTransport();
        
        try{
            log.info("Sending...") ;
            
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect("email-smtp.us-west-2.amazonaws.com", "smtp@deepdreams.tech", "AKIAV2U7KYX2DGBVVC6M,BMzphd4CSWCNYLFtqw/Rxar3MbMCEDR4aoB3ZmT3RkJu");
        	
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            log.info("Email sent!");
        } catch (Exception ex) {
        	log.error("The email was not sent.", ex);
        } finally{
            transport.close();
        }
	}
	
	
}
