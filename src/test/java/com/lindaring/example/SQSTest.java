package com.lindaring.example;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.localstack.LocalStackContainer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SQSTest extends BaseIntegrationTest {

    @Autowired
    private MySQSService mySQSService;

    @Test
    public void firstTest() {
        final AmazonSQS sqs = AmazonSQSClient
                .builder()
                .withEndpointConfiguration(BaseIntegrationTest.localStackContainer.getEndpointConfiguration(LocalStackContainer.Service.SQS))
                .withCredentials(BaseIntegrationTest.localStackContainer.getDefaultCredentialsProvider()).build();
        String mainQueueURL = sqs.createQueue("Main").getQueueUrl();
        Assert.assertTrue(sqs.listQueues().getQueueUrls().contains(mainQueueURL));
    }

}
