package tech.deepdreams.messaging.util;
import java.io.IOException;
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
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.mappers.ReminderEmailMapper;
import tech.deepdreams.messaging.models.ReminderEmail;
import tech.deepdreams.messaging.repositories.ReminderEmailRepository;
import tech.deepdreams.messaging.requests.ReminderEmailPayload;

@Log4j2
@AllArgsConstructor
@Service
public class AmazonEmailSender {
	private SpringTemplateEngine templateEngine ;
    private AmazonSimpleEmailService amazonSEService ;
    private ReminderEmailRepository reminderEmailRepository ;
    private ReminderEmailMapper reminderEmailMapper ;
	 
	
	public ReminderEmailDTO saveReminderEmail(ReminderEmailPayload payload) throws IOException {

		final Context context = new Context();
		
		payload.getTemplateModel().forEach((paramName, paramValue) -> {
			context.setVariable(paramName, paramValue) ;
		}) ;
		
		final String htmlBody = templateEngine.process(payload.getTemplateFile(), context) ;
		
		ReminderEmail reminderEmail = ReminderEmail.builder()
			 .eventType(payload.getEventType())
			 .eventId(payload.getEventId())
   		     .subject(payload.getSubject())
   		     .sender(payload.getFrom())
   		     .recipient(payload.getTo())
   		     .content(htmlBody)
   		     .timestamp(OffsetDateTime.now())
   			 .sent(false)
   		     .build() ;
		
		return reminderEmailMapper.mapModelToDTO(reminderEmailRepository.save(reminderEmail)) ;
	}
	
	
	public void sendReminderEmail(ReminderEmail reminderEmail) throws IOException {
		String from = reminderEmail.getSender() ;
		String to  = reminderEmail.getRecipient() ;
		String subject = reminderEmail.getSubject() ;		
		String htmlBody = reminderEmail.getContent() ;
		
        SendEmailRequest request = new SendEmailRequest()
            .withDestination(new Destination().withToAddresses(to))
            .withMessage(new Message()
                .withBody(new Body()
                    .withHtml(new Content().withCharset("UTF-8").withData(htmlBody)))
                .withSubject(new Content().withCharset("UTF-8").withData(subject)))
            .withSource(from) ;
        
        try {
        	amazonSEService.sendEmail(request) ;
        } catch (Exception ex) {
            log.error(String.format("Unable to resend email '%s' to '%s'", subject, to), ex) ;
        }
	}
	
	
}
