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
@Table(name = "phone_number")
public class PhoneNumber {
	@Id
	private Long id ;
	
	@Column(name = "country_id")
	private Long countryId ;
	
	@NotBlank
	private String number ;

}
