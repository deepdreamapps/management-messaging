package tech.deepdreams.messaging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EntityScan(basePackages = {"tech.deepdreams.messaging.models"})
@EnableJpaRepositories("tech.deepdreams.messaging.repositories")
@SpringBootApplication
public class ManagementMessagingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagementMessagingApplication.class, args);
	}

}
