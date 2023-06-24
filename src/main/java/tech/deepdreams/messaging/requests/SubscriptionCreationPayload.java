package tech.deepdreams.messaging.requests;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionCreationPayload {
	private Long id ;
	
	private String firstName ;
	
	private String lastName ;
	
	private String label ;
	
	private String offerLabel ;
	
	private String applicationLabel ;
	
	private Integer numberOfYears ;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss Z")
	private OffsetDateTime expirationDate ;
	
	private Integer numberOfUsers ;
	
	private Integer numberOfUnits ;
	
	private Integer numberOfRequests ;
	
	private Integer trialDuration ;
	
	private String emailAddress ;
}
