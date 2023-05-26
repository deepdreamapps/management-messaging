package tech.deepdreams.messaging.models;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReminderEmail {

	private Long id ;
	
	private Long eventId ;
	
	private String eventType ;

	private String subject ;
	
	private String content ;
	
	private String recipient ;

}
