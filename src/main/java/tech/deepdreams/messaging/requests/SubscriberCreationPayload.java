package tech.deepdreams.messaging.requests;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriberCreationPayload {
	private String firstName ;
	
	private String lastName ;
	
	private String label ;
	
	private String emailAddress ;
	
	private Long eventId ;
}
