package tech.deepdreams.messaging.models;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "virtual_phone_number")
public class VirtualPhoneNumber {
	@Id
	private Long id ;
	
	@Column(name = "country_id")
	private Long countryId ;
	
	@NotBlank
	@Column(name = "country_number")
	private String countryNumber ;
	
	@NotBlank
	@Column(name = "twilio_number")
	private String twilioNumber ;
	
	@NotBlank
	@Column(name = "account_sid")
	private String accountSid ;
	
	@NotBlank
	@Column(name = "auth_token")
	private String authToken ;

}
