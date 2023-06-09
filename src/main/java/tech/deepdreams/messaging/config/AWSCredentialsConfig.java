package tech.deepdreams.messaging.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

@Profile({"int", "prod"})
@Configuration
public class AWSCredentialsConfig {
	
	@Bean
	public AWSCredentialsProvider credentialsProvider() {
		return new DefaultAWSCredentialsProviderChain();
	}

	@Bean
	public BasicAWSCredentials awsCredentials(AWSCredentialsProvider provider) {
		return new BasicAWSCredentials(provider.getCredentials().getAWSAccessKeyId(),
				provider.getCredentials().getAWSSecretKey());
	}
    
}
