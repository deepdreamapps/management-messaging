package tech.deepdreams.messaging.models;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailIntegration {
	@Id
	private Long id ;
	
	@Column(name = "subscriber_id")
	private Long subscriberId ;
	
	private String hostname ;

	@Column(name = "port_number")
	private int portNumber ;
	
	private String protocol ;
	
	private String email ;
	
	private String password ;

}
