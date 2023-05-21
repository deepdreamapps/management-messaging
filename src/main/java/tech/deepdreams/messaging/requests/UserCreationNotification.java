package tech.deepdreams.messaging.requests;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationNotification {
	private String firstName ;
	
	private String lastName ;
	
	private boolean admin ;
	
	private String emailAddress ;
}
