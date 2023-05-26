package tech.deepdreams.messaging.apiclient;
import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebClientConfiguration {
	
	   @Bean
	   public RestTemplate restTemplate(RetryTemplate retryTemplate) {
	        return new RestTemplateBuilder()
	                		.setConnectTimeout(Duration.ofSeconds(10))
	                		.setReadTimeout(Duration.ofSeconds(10))
	                		.build();
	   }

	   @Bean
	   public RetryTemplate retryTemplate() {
	        RetryTemplate retryTemplate = new RetryTemplate();

	        RetryPolicy retryPolicy = new SimpleRetryPolicy(5); // retry up to 5 times
	        retryTemplate.setRetryPolicy(retryPolicy);

	        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
	        backOffPolicy.setInitialInterval(15000);
	        backOffPolicy.setMultiplier(1.5);
	        backOffPolicy.setMaxInterval(30000);
	        retryTemplate.setBackOffPolicy(backOffPolicy);

	        return retryTemplate;
	   }
	  
	  

}
