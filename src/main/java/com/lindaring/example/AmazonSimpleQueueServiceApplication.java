package com.lindaring.example;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.extern.slf4j.Slf4j;
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

	public static void main(String[] args) {
		SpringApplication.run(AmazonSimpleQueueServiceApplication.class, args);
	}

	@PostMapping
	public void sendMessage(@RequestBody String notificationData) {
		try {
			final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
			log.info("Sending a message to MyQueue.\n");
			sqs.sendMessage(new SendMessageRequest(sqsURL, notificationData));
			log.info("Message Sent.\n");

		} catch (final AmazonServiceException ase) {
			log.error("Caught an AmazonServiceException, which means " +
					"your request made it to Amazon SQS, but was " +
					"rejected with an error response for some reason.");
			log.error("Error Message:    " + ase.getMessage());
			log.error("HTTP Status Code: " + ase.getStatusCode());
			log.error("AWS Error Code:   " + ase.getErrorCode());
			log.error("Error Type:       " + ase.getErrorType());
			log.error("Request ID:       " + ase.getRequestId());
		} catch (final AmazonClientException ace) {
			log.error("Caught an AmazonClientException, which means " +
					"the client encountered a serious internal problem while " +
					"trying to communicate with Amazon SQS, such as not " +
					"being able to access the network.");
			log.error("Error Message: " + ace.getMessage());
		}
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
