package com.lindaring.example.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class SQSService {

    @Autowired
    private ApplicationContext applicationContext;

    private AmazonSQS sqsClient;

    @Value("${sqs.url}")
    private String sqsURL;

    public boolean sendQueueMessage(String notificationData) {
        boolean sent = false;
        try {
            log.info("Sending a message to MyQueue.\n");
            sqsClient.sendMessage(new SendMessageRequest(sqsURL, notificationData));
            log.info("Message Sent.\n");
            sent = true;
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
        return sent;
    }

    public String readQueueMessage() {
        while (true) {
            log.info("Receiving messages from MyQueue.\n");

            final ReceiveMessageRequest receiveMessageRequest =
                    new ReceiveMessageRequest(sqsURL)
                            .withMaxNumberOfMessages(1)
                            .withWaitTimeSeconds(3);

            log.info("Message: " + sqsClient.receiveMessage(receiveMessageRequest).getMessages());
        }
    }

    @PostConstruct
    private void init() {
        sqsClient = (AmazonSQS) applicationContext.getBean("SQSClient");
    }

}
