package tech.deepdreams.messaging.controlers;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.services.ReminderEmailService;


@RestController
@RequestMapping("/api/messaging")
@AllArgsConstructor
public class ReminderEmailCtrl {
	private ReminderEmailService reminderEmailService ;
	
	
	
	@GetMapping("/email/{startDate}/{endDate}")
	public List<ReminderEmailDTO> fetchEmailsBetween(@PathVariable("startDate")  String startDateStr, 
			@PathVariable("endDate") String endDateStr) {
		LocalDate startDate = LocalDate.parse(startDateStr) ;
		LocalDate endDate = LocalDate.parse(endDateStr) ;
		return reminderEmailService.fetchEmailsBetween(startDate, endDate) ;
	}
}
