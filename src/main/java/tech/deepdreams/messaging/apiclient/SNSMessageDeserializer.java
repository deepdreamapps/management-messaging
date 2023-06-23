package tech.deepdreams.messaging.apiclient;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class SNSMessageDeserializer extends JsonDeserializer<SNSMessage>{
	
	
	@Override
    public SNSMessage deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        String Type = jsonNode.get("Type").asText() ;
        String MessageId = jsonNode.get("MessageId").asText() ;
        String TopicArn = jsonNode.get("TopicArn").asText() ;
        String Message = jsonNode.get("Message").asText() ;
        String Timestamp = jsonNode.get("Timestamp").asText() ;
        String SignatureVersion = jsonNode.get("SignatureVersion").asText() ;
        String Signature = jsonNode.get("Signature").asText() ;
        String SigningCertURL = jsonNode.get("SigningCertURL").asText() ;
        String UnsubscribeURL = jsonNode.get("UnsubscribeURL").asText() ;
        
        return SNSMessage.builder()
        		    	.Type(Type) 
        		    	.MessageId(MessageId)
        		    	.TopicArn(TopicArn)
        		    	.Message(Message)
        		    	.Timestamp(Timestamp)
        		    	.SignatureVersion(SignatureVersion)
        		    	.Signature(Signature)
        		    	.SigningCertURL(SigningCertURL)
        		    	.UnsubscribeURL(UnsubscribeURL)
        		    	.build() ;
    }

}