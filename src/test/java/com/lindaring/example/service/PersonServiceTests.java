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

    private SqsClient sqsClient;

    @Autowired
    private JmsConfig jmsConfig;

    @Value("${sqs.queues.create-person}")
    private String createPersonQueue;

    @Autowired
    private PersonService personService;

    @Before
    public void setup() {
        sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(jmsConfig.getProtocol() + "://" + jmsConfig.getHost() + ":" + jmsConfig.getPort()))
                .build();
    }

    @Test
    public void CAN_CREATE_QUEUE() {
        CreateQueueResponse queueResponse = sqsClient.createQueue(CreateQueueRequest.builder().queueName(createPersonQueue).build());
        Assert.assertTrue(sqsClient.listQueues().queueUrls().contains(queueResponse.queueUrl()));
    }

    @Test
    public void CAN_SEND_QUEUE_MESSAGE() {
        personService.initPersonCreation(new Person("John", "Doe"));
    }

}
