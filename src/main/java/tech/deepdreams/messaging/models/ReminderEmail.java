package tech.deepdreams.messaging.models;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "reminder_email")
@Entity
public class ReminderEmail {
    @Id
	private Long id ;
	
	@Column(name = "event_id")
	private Long eventId ;
	
	@Column(name = "event_type")
	private String eventType ;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss ")
	private OffsetDateTime instant ;

	private String subject ;
	
	private String content ;
	
	private String sender ;
	
	private String recipient ;
	
	private boolean sent ;
}
