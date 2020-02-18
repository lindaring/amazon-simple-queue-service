package com.lindaring.example.service;

import com.lindaring.example.config.JmsConfig;
import com.lindaring.example.model.Person;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;

import java.net.URI;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class PersonServiceTests {

    @Autowired
    private JmsConfig jmsConfig;

    @Autowired
    protected JmsTemplate defaultJmsTemplate;

    @Value("${sqs.queues.create-person}")
    private String createPersonQueue;

    private SqsClient sqsClient;
    private CreateQueueResponse queueResponse;

    @Before
    public void setup() {
        String url = jmsConfig.getProtocol() + "://" + jmsConfig.getHost() + ":" + jmsConfig.getPort();
        sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(url))
                .build();
        queueResponse = sqsClient.createQueue(CreateQueueRequest.builder().queueName(createPersonQueue).build());
    }

    @Test
    public void CAN_CREATE_QUEUE() {
        Assert.assertTrue(sqsClient.listQueues().queueUrls().contains(queueResponse.queueUrl()));
    }

    @Test
    public void CAN_SEND_QUEUE_MESSAGE() {
        defaultJmsTemplate.convertAndSend(createPersonQueue, new Person("John", "Doe"));
    }

    @Test
    public void CAN_RECEIVE_QUEUE_MESSAGE() {
        defaultJmsTemplate.receiveAndConvert(createPersonQueue);
    }

}
