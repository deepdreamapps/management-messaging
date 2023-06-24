package tech.deepdreams.messaging.endpoints;
import java.time.OffsetDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.services.BillService;
import tech.deepdreams.messaging.services.ReminderEmailService;

@Log4j2
@AllArgsConstructor
@Service
public class BillingEndpoint {
	private BillService  billService ;
	private ReminderEmailService reminderEmailService ;
	
	
	@Scheduled(fixedDelay = 30_000)
	public void handleCreation () {
		log.info(String.format("BillingEndpoint.handleCreation : Execution time %s", OffsetDateTime.now())) ;
		billService.fetchFromCreatedQueue().stream()
				   .map(event  -> {
					   return billService.fetchBill(event.getBillId()) ;
				   })
				   .map(bill  -> {
					   ReminderEmailDTO reminderEmailDTO = null ;
	        		   try {
	        			   reminderEmailDTO = billService.genReminderEmail(bill) ;
		        	   } catch (Exception e) {
		        		   log.error(String.format("Unable to generate reminder email from : %s", bill), e) ;
		        	   }
	        		   return reminderEmailDTO ;
				   })
		           .forEach(reminderEmailDTO -> {
	        		   try {
	        			   reminderEmailService.sendReminderEmail(reminderEmailDTO) ;
		        	   } catch (Exception e) {
		        		   log.error(String.format("Unable to send reminder email : %s", reminderEmailDTO), e) ;
		        		   return ;
		        	   }
		           }) ;
	}
	

}
