package tech.deepdreams.messaging.models;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReminderSms {

	private Long countryId ;

	private String sender ;

	private String subject ;
	
	private String content ;
	
	private String recipient ;

}
