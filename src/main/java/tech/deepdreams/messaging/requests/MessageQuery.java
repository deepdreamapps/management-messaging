package tech.deepdreams.messaging.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageQuery {
  private Long eventId ;
  
  private String eventType ;
}
