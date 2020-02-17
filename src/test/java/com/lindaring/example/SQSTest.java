package com.lindaring.example;

import com.amazonaws.services.sqs.AmazonSQS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ActiveProfiles("junit")
@RunWith(SpringRunner.class)
public class SQSTest {

    private final String QUEUE_NAME = "unit-test-queue";

    @Autowired
    private ApplicationContext applicationContext;

    private AmazonSQS sqsClient;

    @Before
    public void setup() {
        sqsClient = (AmazonSQS) applicationContext.getBean("SQSClient");
    }

    @Test
    public void CAN_CREATE_QUEUE() {
        String queueUrl = sqsClient.createQueue(QUEUE_NAME).getQueueUrl();
        Assert.assertTrue(sqsClient.listQueues().getQueueUrls().contains(queueUrl));
    }

}
