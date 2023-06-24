package tech.deepdreams.messaging.apiclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SecretsManagerClient {
	@Value("${identity.defaultPassword.secretname}")
	private String secretName;
	
	@Autowired
	private AWSSecretsManager secretsManager;
	
	
	public String getDefaultPassword() {
		GetSecretValueRequest secretRequest = new GetSecretValueRequest().withSecretId(secretName);
		GetSecretValueResult secretResult = secretsManager.getSecretValue(secretRequest);
		return secretResult.getSecretString();
	}
}
