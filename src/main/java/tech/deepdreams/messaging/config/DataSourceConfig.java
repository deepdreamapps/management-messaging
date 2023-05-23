package tech.deepdreams.messaging.config;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
	@Value("${database.url}")
    private String url ;
	
	@Value("${database.driver-class-name}")
	private String driverClassName ;
	
	@Value("${database.schema}")
	private String schema ;
	
	@Value("${database.initial-size}")
	private Integer initialSize ;
	
	@Value("${database.max-size}")
	private Integer maxSize ;
	
	@Value("${database.max-lifetime}")
	private Long maxLifetime ;

	@Autowired
	private Environment env ;


    @Bean
    public DataSource dataSource() {
    	String userName = env.getProperty("db_username") ;
		String password = env.getProperty("db_password") ;
	    
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setSchema(schema) ;
        dataSource.setMaximumPoolSize(maxSize);
        dataSource.setMaxLifetime(maxLifetime);
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
