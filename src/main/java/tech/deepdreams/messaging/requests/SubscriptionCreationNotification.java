package tech.deepdreams.messaging.requests;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionCreationNotification {
	private String firstName ;
	
	private String lastName ;
	
	private String label ;
	
	private String offerLabel ;
	
	private String applicationLabel ;
	
	private Integer numberOfYears ;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss Z")
	private ZonedDateTime expirationDate ;
	
	private Integer numberOfUsers ;
	
	private Integer numberOfUnits ;
	
	private Integer numberOfRequests ;
	
	private Integer trialDuration ;
	
	private String emailAddress ;
}
