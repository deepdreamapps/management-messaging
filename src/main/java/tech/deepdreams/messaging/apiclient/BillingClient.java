package tech.deepdreams.messaging.apiclient;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import tech.deepdreams.billing.events.BillCreatedEvent;
import tech.deepdreams.messaging.dtos.BillDTO;

@Log4j2
@Service
public class BillingClient {
	@Value("${billing.queueBillCreatedUrl}")
	private String queueBillCreatedUrl ;
	
	@Value("${billing.fetchBillByIdUrl}")
	private String fetchBillByIdUrl ;
	
	@Autowired
	private RestTemplate restTemplate ;
	
	@Autowired
	private AmazonSQSClient amazonSQSClient ;
	
	@Autowired
	private ObjectMapper objectMapper ;
	

	public List<BillCreatedEvent> fetchFromCreatedQueue() throws JsonMappingException, JsonProcessingException{
		List<BillCreatedEvent> listOfEvents = new ArrayList<>() ;
		ReceiveMessageResult result = amazonSQSClient.receiveMessage(queueBillCreatedUrl) ;
		for(Message message : result.getMessages()) {
			SNSMessage SNSMessage = objectMapper.readValue(message.getBody(), SNSMessage.class) ;
			BillCreatedEvent event = objectMapper.readValue(SNSMessage.getMessage(), BillCreatedEvent.class) ;
			listOfEvents.add(event) ;
			log.info(String.format("Message retrieved from the queue %s", message)) ;
			amazonSQSClient.deleteMessage(queueBillCreatedUrl, message.getReceiptHandle()) ;
		}
		log.info(String.format("Number of messages retrieved from the queue %s", listOfEvents.size())) ;
        return listOfEvents ;
	}
	
	
	public BillDTO fetchBill(Long billId) {
		log.info(String.format("Calling Billing API to get bill with billId %d", billId)) ;
		BillDTO bill = restTemplate.getForObject(fetchBillByIdUrl, BillDTO.class, billId) ;
		return bill ;
    }
}
