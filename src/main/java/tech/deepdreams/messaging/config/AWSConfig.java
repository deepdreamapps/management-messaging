package tech.deepdreams.messaging.config;
import org.springframework.beans.factory.annotation.Value ;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tech.deepdreams.subscriber.events.SubscriberCreatedEvent;
import tech.deepdreams.subscriber.events.SubscriberSuspendedEvent;
import tech.deepdreams.billing.deserializers.BillCreatedEventDeserializer;
import tech.deepdreams.billing.deserializers.BillExpiredEventDeserializer;
import tech.deepdreams.billing.events.BillCreatedEvent;
import tech.deepdreams.billing.events.BillExpiredEvent;
import tech.deepdreams.messaging.apiclient.SNSMessage;
import tech.deepdreams.messaging.apiclient.SNSMessageDeserializer;
import tech.deepdreams.subscription.deserializers.AdvancedSecurityDisabledEventDeserializer;
import tech.deepdreams.subscription.deserializers.AdvancedSecurityEnabledEventDeserializer;
import tech.deepdreams.subscription.deserializers.AdvancedSecurityRequestedEventDeserializer;
import tech.deepdreams.subscription.deserializers.AutomaticPaymentEnabledEventDeserializer;
import tech.deepdreams.subscription.deserializers.CapacityIncreaseCancelledEventDeserializer;
import tech.deepdreams.subscription.deserializers.CapacityIncreaseCompletedEventDeserializer;
import tech.deepdreams.subscription.deserializers.CapacityIncreaseRequestedEventDeserializer;
import tech.deepdreams.subscription.deserializers.SubscriptionActivatedEventDeserializer;
import tech.deepdreams.subscription.deserializers.SubscriptionCancelledEventDeserializer;
import tech.deepdreams.subscription.deserializers.SubscriptionCreatedEventDeserializer;
import tech.deepdreams.subscription.deserializers.SubscriptionExpiredEventDeserializer;
import tech.deepdreams.subscription.deserializers.SubscriptionRenewedEventDeserializer;
import tech.deepdreams.subscription.deserializers.SubscriptionSuspendedEventDeserializer;
import tech.deepdreams.subscription.deserializers.SubscriptionUpgradedEventDeserializer;
import tech.deepdreams.subscription.events.AdvancedSecurityDisabledEvent;
import tech.deepdreams.subscription.events.AdvancedSecurityEnabledEvent;
import tech.deepdreams.subscription.events.AdvancedSecurityRequestedEvent;
import tech.deepdreams.subscription.events.AutomaticPaymentEnabledEvent;
import tech.deepdreams.subscription.events.CapacityIncreaseCancelledEvent;
import tech.deepdreams.subscription.events.CapacityIncreaseCompletedEvent;
import tech.deepdreams.subscription.events.CapacityIncreaseRequestedEvent;
import tech.deepdreams.subscription.events.SubscriptionActivatedEvent;
import tech.deepdreams.subscription.events.SubscriptionCancelledEvent;
import tech.deepdreams.subscription.events.SubscriptionCreatedEvent;
import tech.deepdreams.subscription.events.SubscriptionExpiredEvent;
import tech.deepdreams.subscription.events.SubscriptionRenewedEvent;
import tech.deepdreams.subscription.events.SubscriptionSuspendedEvent;
import tech.deepdreams.subscription.events.SubscriptionUpgradedEvent;
import tech.deepdreams.subscriber.deserializers.SubscriberCreatedEventDeserializer;
import tech.deepdreams.subscriber.deserializers.SubscriberSuspendedEventDeserializer;

@Configuration
public class AWSConfig {
	@Value("${aws.region}")
	private String region;


	@Bean
	public AWSSecretsManager secretsManager(AWSCredentialsProvider provider) {
		return AWSSecretsManagerClientBuilder.standard().withCredentials(provider).withRegion(region).build();
	}

	@Bean
	public AmazonSQSClient amazonSQSClient() {
		return (AmazonSQSClient) AmazonSQSClientBuilder.standard().build();
	}


	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(SubscriberCreatedEvent.class, new SubscriberCreatedEventDeserializer());
		module.addDeserializer(SubscriberSuspendedEvent.class, new SubscriberSuspendedEventDeserializer());
		
		module.addDeserializer(SubscriptionCreatedEvent.class, new SubscriptionCreatedEventDeserializer()) ;
		module.addDeserializer(SubscriptionActivatedEvent.class, new SubscriptionActivatedEventDeserializer()) ;
		module.addDeserializer(SubscriptionSuspendedEvent.class, new SubscriptionSuspendedEventDeserializer()) ;
		module.addDeserializer(SubscriptionRenewedEvent.class, new SubscriptionRenewedEventDeserializer()) ;
		module.addDeserializer(SubscriptionCancelledEvent.class, new SubscriptionCancelledEventDeserializer()) ;
		module.addDeserializer(SubscriptionExpiredEvent.class, new SubscriptionExpiredEventDeserializer()) ;
		module.addDeserializer(SubscriptionUpgradedEvent.class, new SubscriptionUpgradedEventDeserializer()) ;
		
		module.addDeserializer(CapacityIncreaseRequestedEvent.class, new CapacityIncreaseRequestedEventDeserializer()) ;
		module.addDeserializer(CapacityIncreaseCompletedEvent.class, new CapacityIncreaseCompletedEventDeserializer()) ;
		module.addDeserializer(CapacityIncreaseCancelledEvent.class, new CapacityIncreaseCancelledEventDeserializer()) ;
		
		module.addDeserializer(AdvancedSecurityRequestedEvent.class, new AdvancedSecurityRequestedEventDeserializer()) ;
		module.addDeserializer(AdvancedSecurityEnabledEvent.class, new AdvancedSecurityEnabledEventDeserializer()) ;
		module.addDeserializer(AdvancedSecurityDisabledEvent.class, new AdvancedSecurityDisabledEventDeserializer()) ;
		
		module.addDeserializer(AutomaticPaymentEnabledEvent.class, new AutomaticPaymentEnabledEventDeserializer()) ;
		
		module.addDeserializer(BillCreatedEvent.class, new BillCreatedEventDeserializer()) ;
		module.addDeserializer(BillExpiredEvent.class, new BillExpiredEventDeserializer()) ;
		
		module.addDeserializer(SNSMessage.class, new SNSMessageDeserializer());
		
		mapper.registerModule(module);
		mapper.registerModule(new JavaTimeModule());
		return mapper;
	}
}
