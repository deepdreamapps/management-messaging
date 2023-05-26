package tech.deepdreams.messaging.services;
import java.io.IOException;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.mappers.ReminderEmailMapper;
import tech.deepdreams.messaging.util.AmazonEmailSender;

@Log4j2
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ReminderEmailService {
	
	@Autowired
	private AmazonEmailSender amazonEmailSender ;
	
	@Autowired
	private ReminderEmailMapper reminderEmailMapper ;
	
	
	
	

	public void sendReminderEmail (ReminderEmailDTO reminderEmailDTO){
		try {
			amazonEmailSender.sendReminderEmail(reminderEmailMapper.mapDTOToModel(reminderEmailDTO)) ;
		} catch (IOException | MessagingException e) {
			log.info(String.format("Reminder email not sent: %s", reminderEmailDTO)) ;
		}
	}
	
}
