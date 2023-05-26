package tech.deepdreams.messaging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ManagementMessagingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagementMessagingApplication.class, args);
	}

}
