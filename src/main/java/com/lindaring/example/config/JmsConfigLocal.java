package com.lindaring.example.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;

@Profile("local")
@Configuration
@EnableJms
public class JmsConfigLocal {

    @Value("${sqs.protocol}") private String protocol;
    @Value("${sqs.host}") private String host;
    @Value("${sqs.port}") private int port;

    /**
     * Expects to have a LocalStack instance already running on localhost. It attempts to connect to that instance.
     */
    @Bean
    @Profile("local")
    SQSConnectionFactory getLocalConnectionFactory() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.valueOf(protocol));
        clientConfiguration.setProxyHost(host);
        clientConfiguration.setProxyPort(port);

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
        factory.setConcurrency("1");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public JmsTemplate defaultJmsTemplate(SQSConnectionFactory sqsConnectionFactory) {
        return new JmsTemplate(sqsConnectionFactory);
    }

}
