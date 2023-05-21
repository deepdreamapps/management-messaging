package tech.deepdreams.messaging.util;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Email {

	private String subject ;
	
	private String template ;
	
	private String content ;
	
	private Map<String, Object> templateModel ;
	
	private String sender ;
	
	private String recipient ;
}
