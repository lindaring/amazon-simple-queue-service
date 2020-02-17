package com.lindaring.example;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@SpringBootApplication
public class AmazonSimpleQueueServiceApplication {

	@Value("${sqs.url}")
	private String sqsURL;

	@Autowired
	private MySQSService mySQSService;

	public static void main(String[] args) {
		SpringApplication.run(AmazonSimpleQueueServiceApplication.class, args);
	}

	@PostMapping
	public void sendMessage(@RequestBody String notificationData) {
		mySQSService.sendQueueMessage(notificationData);
	}

	@GetMapping
	public String readMessage() {
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

		while (true) {
			log.info("Receiving messages from MyQueue.\n");

			final ReceiveMessageRequest receiveMessageRequest =
					new ReceiveMessageRequest(sqsURL)
							.withMaxNumberOfMessages(1)
							.withWaitTimeSeconds(3);

			log.info("Message: " + sqs.receiveMessage(receiveMessageRequest).getMessages());
		}
	}

}
