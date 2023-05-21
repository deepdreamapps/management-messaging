package tech.deepdreams.messaging.endpoints;
import java.time.ZonedDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProbesEndpoint {
	
	
	@GetMapping(path = "/liveness")
	public String livenessProbe () {
		return  String.format("Alive at %s", ZonedDateTime.now()) ;
	}
	
	@GetMapping(path = "/readiness")
	public String readinessProbe () {
		return String.format("Ready at %s :  registred events.", ZonedDateTime.now()) ; 
	}
}
