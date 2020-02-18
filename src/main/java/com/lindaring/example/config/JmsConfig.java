package com.lindaring.example.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.testcontainers.containers.localstack.LocalStackContainer;

import javax.jms.Session;

@EnableJms
@Configuration
@Getter
public class JmsConfig {

    @Value("${sqs.protocol}") private String protocol;
    @Value("${sqs.host}") private String host;
    @Value("${sqs.port}") private int port;

    @Bean
    @Profile("test")
    SQSConnectionFactory getTestConnectionFactory() {
        LocalStackContainer localStackContainer = new LocalStackContainer().withServices(LocalStackContainer.Service.SQS);
        localStackContainer.start();

        String endpoint = localStackContainer.getEndpointConfiguration(LocalStackContainer.Service.SQS).getServiceEndpoint();
        String region = localStackContainer.getEndpointConfiguration(LocalStackContainer.Service.SQS).getSigningRegion();

        protocol = endpoint.substring(0, endpoint.indexOf(":"));
        host = endpoint.substring(endpoint.indexOf("//") + 2, endpoint.lastIndexOf(":"));
        port = Integer.parseInt(endpoint.substring(endpoint.lastIndexOf(":") + 1));

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.valueOf(protocol.toUpperCase()));
        clientConfiguration.setProxyHost(host);
        clientConfiguration.setProxyPort(port);

        return SQSConnectionFactory.builder()
                .withClientConfiguration(clientConfiguration)
                .withRegion(Region.getRegion(Regions.valueOf(region.toUpperCase().replaceAll("-","_"))))
                .withAWSCredentialsProvider(new DefaultAWSCredentialsProviderChain())
                .build();
    }

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
    @Profile("prod")
    SQSConnectionFactory getDevConnectionFactory() {
        return SQSConnectionFactory.builder()
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
