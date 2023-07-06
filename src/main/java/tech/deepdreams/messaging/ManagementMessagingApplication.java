package tech.deepdreams.messaging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.twilio.Twilio;

@EnableScheduling
@SpringBootApplication
public class ManagementMessagingApplication {

	public static void main(String[] args) {
		Twilio.init("", "");
		SpringApplication.run(ManagementMessagingApplication.class, args);
	}

}
