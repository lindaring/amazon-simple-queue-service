package com.lindaring.example.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;

@EnableJms
@Configuration
public class JmsConfig {

    @Bean
    SQSConnectionFactory getSQSConnectionFactory() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTP);
        clientConfiguration.setProxyHost("localhost");
        clientConfiguration.setProxyPort(4576);
        return SQSConnectionFactory.builder()
                .withClientConfiguration(clientConfiguration)
                .withRegion(Region.getRegion(Regions.EU_WEST_1))
                .withAWSCredentialsProvider(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(SQSConnectionFactory sqsConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(sqsConnectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("3-10");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public JmsTemplate defaultJmsTemplate(SQSConnectionFactory sqsConnectionFactory) {
        return new JmsTemplate(sqsConnectionFactory);
    }

}
