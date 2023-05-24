package tech.deepdreams.messaging.config;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Profile({"dev", "int", "prod"})
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {
	@Value("${database.driver-class-name}")
	private String driverClassName ;
	
	@Value("${database.hostname}")
	private String hostName ;
	
	@Value("${database.url}")
	private String jdbcUrl ;
	
	@Value("${database.port}")
	private Integer port ;
	
	@Value("${database.username}")
	private String userName ;
	
	@Value("${database.secret}")
	private String secretName ;
	
	@Value("${database.name}")
	private String databaseName ;
	
	@Value("${database.schema}")
	private String schema ;
	
	@Value("${database.initial-size}")
	private Integer initialSize ;
	
	@Value("${database.max-size}")
	private Integer maxSize ;
	
	@Value("${database.max-lifetime}")
	private Integer maxLifetime ;
	
	@Autowired
	private AWSSecretsManager secretsManager ;
	
	
	@Bean
    public DataSource dataSource() {

		GetSecretValueRequest request = new GetSecretValueRequest()
	            .withSecretId(secretName) ;
	
		GetSecretValueResult result = secretsManager.getSecretValue(request) ;

	    String password = result.getSecretString();
	    
		HikariDataSource dataSource = new HikariDataSource() ;
		dataSource.setDriverClassName(driverClassName) ;
		dataSource.setJdbcUrl(jdbcUrl) ;
		dataSource.setUsername(userName) ;
		dataSource.setPassword(password) ;
		dataSource.setKeepaliveTime(maxLifetime) ;
		dataSource.setMaximumPoolSize(maxSize);
		dataSource.setSchema(schema) ;
        return dataSource ;
    }
	
	
	@Bean
	   public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
	      LocalContainerEntityManagerFactoryBean em 
	        = new LocalContainerEntityManagerFactoryBean();
	      em.setDataSource(dataSource());
	      em.setPackagesToScan(new String[] { "tech.deepdreams.messaging" });
	      
	      Properties properties = new Properties();
	      properties.setProperty("hibernate.hbm2ddl.auto", "none");
	      properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

	      JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	      em.setJpaVendorAdapter(vendorAdapter);
	      em.setJpaProperties(properties);

	      return em;
	   }
}
