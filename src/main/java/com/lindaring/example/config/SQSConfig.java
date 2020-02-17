package com.lindaring.example.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;

@Configuration
public class SQSConfig {

    @Bean
    @Profile("junit")
    public AmazonSQS SQSClient() {
        LocalStackContainer localStackContainer = new LocalStackContainer().withServices(LocalStackContainer.Service.SQS);
        localStackContainer.start();
        return AmazonSQSClient.builder()
                .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(LocalStackContainer.Service.SQS))
                .withCredentials(localStackContainer.getDefaultCredentialsProvider()).build();
    }

}
