package tech.deepdreams.messaging.config;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
	@Value("${database.url}")
    private String url ;

    @Value("${database.secret}")
    private String secretName ;
    
    @Autowired
	private AWSSecretsManager secretsManager ;


    @Bean
    public DataSource dataSource() {
    	GetSecretValueRequest request = new GetSecretValueRequest()
	            .withSecretId(secretName) ;
	        
		GetSecretValueResult result = secretsManager.getSecretValue(request);
	       
		String secretValue = result.getSecretString();

	    // Parse the secret value as a JsonNode
	    ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode secretNode = null ;
	    
		try {
			secretNode = objectMapper.readTree(secretValue);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException("Bad secrets found !", ex) ;
		}

	    // Access the values by field name
	    String username = secretNode.get("username").asText();
	    String password = secretNode.get("password").asText();
	    
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
    
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.show_sql", false);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("tech.deepdreams");
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaProperties(jpaProperties);

        return factoryBean;
    }
}
