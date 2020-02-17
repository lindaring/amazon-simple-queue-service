package com.lindaring.example.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;

@Configuration
public class SQSConfig {

    @Bean(name = "SQSClient")
    @Profile("junit")
    public AmazonSQS getUnitTestQueueClient() {
        LocalStackContainer localStackContainer = new LocalStackContainer().withServices(LocalStackContainer.Service.SQS);
        localStackContainer.start();
        return AmazonSQSClient.builder()
                .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(LocalStackContainer.Service.SQS))
                .withCredentials(localStackContainer.getDefaultCredentialsProvider()).build();
    }

    @Bean(name = "SQSClient")
    @Profile("local")
    public AmazonSQS getLocalQueueClient() {
        return  AmazonSQSClientBuilder.defaultClient();
    }

}
