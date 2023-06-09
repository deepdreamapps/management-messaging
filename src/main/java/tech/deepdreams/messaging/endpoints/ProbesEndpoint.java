package tech.deepdreams.messaging.endpoints;
import java.time.ZonedDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class ProbesEndpoint {
	
	@GetMapping(path = "/api/messaging/liveness")
	public String livenessProbe () {
		return  String.format("Alive at %s", ZonedDateTime.now()) ;
	}
	
	
	@GetMapping(path = "/api/messaging/readiness")
	public String readinessProbe () {
		return String.format("Ready at %s ", ZonedDateTime.now()) ; 
	}
}
