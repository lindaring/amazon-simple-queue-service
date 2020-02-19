package com.lindaring.example.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.localstack.LocalStackContainer;

@Configuration
public class AmazonConfig {

    @Bean
    public AmazonSQS getAmazonSQS() {
        return  AmazonSQSClientBuilder.defaultClient();
    }

}
