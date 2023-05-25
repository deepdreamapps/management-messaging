package tech.deepdreams.messaging.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tech.deepdreams.subscriber.events.SubscriberCreatedEvent;
import tech.deepdreams.subscriber.events.serializers.SubscriberCreatedEventSerializer;


@Configuration
public class AWSConfig {
	@Value("${aws.region}")
    private String region ;
	
	
	@Bean
    public AWSSecretsManager secretsManager(AWSCredentialsProvider provider) {
        return AWSSecretsManagerClientBuilder.standard() 
        		          .withCredentials(provider) 
        		          .withRegion(region)
        		          .build() ;
    }
	
	
	@Bean
	public AWSCredentialsProvider credentialsProvider () {
		return new DefaultAWSCredentialsProviderChain();
	}
	
	
	@Bean
	public BasicAWSCredentials awsCredentials (AWSCredentialsProvider provider) {
		return new BasicAWSCredentials(provider.getCredentials().getAWSAccessKeyId(),
				provider.getCredentials().getAWSSecretKey());
	}
	
	
	
	@Bean
    public AmazonSQSClient amazonSQSClient() {
        return (AmazonSQSClient) AmazonSQSClientBuilder.standard().build();
    }
    
    
	@Bean
	public AmazonSimpleEmailService  amazonSESClient(BasicAWSCredentials awsCredentials) {
		return AmazonSimpleEmailServiceClientBuilder.standard()
	            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
	            .withRegion(Regions.fromName(region))
	            .build();
	}
	
	
	 @Primary
	    @Bean
	    public ObjectMapper objectMapper() {
	    	ObjectMapper mapper = new ObjectMapper();
	    	SimpleModule module = new SimpleModule();
	    	module.addSerializer(SubscriberCreatedEvent.class, new SubscriberCreatedEventSerializer());
	    	
	    	mapper.registerModule(module);
	    	mapper.registerModule(new JavaTimeModule()) ;
	    	return mapper ;
	    }
}
