package com.lindaring.example;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MySQSService {

    @Value("${sqs.url}")
    private String sqsURL;

    public boolean sendQueueMessage(String notificationData) {
        boolean sent = false;
        try {
            final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            log.info("Sending a message to MyQueue.\n");
//            sqs.createQueue("")
//            sqs.setEndpoint("http://localhost:4576/123456789012/test_queue");
//            sqs.createQueue("test_queue");
            CreateQueueRequest createQueueRequest = new CreateQueueRequest("test_queue");
            sqs.createQueue(createQueueRequest);
            sqs.sendMessage(new SendMessageRequest(sqsURL, notificationData));
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

}
