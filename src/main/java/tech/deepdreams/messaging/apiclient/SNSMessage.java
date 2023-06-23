package tech.deepdreams.messaging.apiclient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SNSMessage {
	private String Type ;
	private String MessageId ;
	private String TopicArn ;
	private String Message ;
	private String Timestamp ;
	private String SignatureVersion ;
	private String Signature ;
	private String SigningCertURL ;
	private String UnsubscribeURL ;
}
