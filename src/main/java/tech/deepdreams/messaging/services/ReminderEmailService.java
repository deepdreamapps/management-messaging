package tech.deepdreams.messaging.services;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.mappers.ReminderEmailMapper;
import tech.deepdreams.messaging.repositories.ReminderEmailRepository;
import tech.deepdreams.messaging.util.AmazonEmailSender;

@Log4j2
@AllArgsConstructor
@Service
public class ReminderEmailService {
	private ReminderEmailRepository reminderEmailRepository ;
	private ReminderEmailMapper reminderEmailMapper ;
	private AmazonEmailSender amazonEmailSender ;
	
	
	public Optional<Boolean> existsByEventTypeAndEventId (String eventType, Long eventId){
		return reminderEmailRepository.existsByEventTypeAndEventId(eventType, eventId) ;
	}
	
	
	public void sendReminderEmail (ReminderEmailDTO reminderEmail){
		reminderEmailRepository.findById(reminderEmail.getId())
				     .ifPresent(email -> {
				    	 try {
							amazonEmailSender.sendReminderEmail(email) ;
							email.setSent(true) ;
					    	reminderEmailRepository.save(email) ;
						} catch (Exception e) {
							log.error(String.format("Message sent : %s", email), e) ;
						}
				     }) ;
	}
	

	public List<ReminderEmailDTO> fetchUndeliveredEmails() {
		return reminderEmailRepository.findBySent(false)
				        .stream()
				        .map(reminderEmail -> {
				        	return reminderEmailMapper.mapModelToDTO(reminderEmail) ;
				        })
				        .collect(Collectors.toList()) ;
	}
	
	
	public List<ReminderEmailDTO> fetchEmailsBetween(LocalDate startDate, LocalDate endDate) {
		OffsetDateTime startOffsetDateTime = OffsetDateTime.of(startDate, LocalTime.MIDNIGHT, ZoneOffset.UTC) ;
		OffsetDateTime endOffsetDateTime = OffsetDateTime.of(endDate, LocalTime.MAX, ZoneOffset.UTC) ;
		return reminderEmailRepository.findByTimestampBetween(startOffsetDateTime, endOffsetDateTime)
				        .stream()
				        .map(reminderEmail -> {
				        	return reminderEmailMapper.mapModelToDTO(reminderEmail) ;
				        })
				        .collect(Collectors.toList()) ;
	}	
}
