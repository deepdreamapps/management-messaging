package tech.deepdreams.messaging.requests;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReminderEmailPayload {
	private String eventType ;
	
	private Long eventId ;
	
	private String from ;
	
	private String to ;
	
	private String subject ;
	
	private String templateFile ;
	
	private Map<String, Object> templateModel ;
}
