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
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

import javax.jms.Session;
import java.net.URI;

@Profile("test")
@Configuration
@EnableJms
public class JmsConfigTest {

    @Value("${sqs.queues.create-person}")
    private String createPersonQueue;

    private String protocol;
    private String host;
    private int port;

    /**
     * Will start a LocalStack with SQS service in a docker container specifically for unit test
     * and will bring it down once tests have been complete.
     */
    @Bean
    SQSConnectionFactory getTestConnectionFactory() {
        LocalStackContainer localStackContainer = new LocalStackContainer().withServices(LocalStackContainer.Service.SQS);
        localStackContainer.start();

        String endpoint = localStackContainer.getEndpointConfiguration(LocalStackContainer.Service.SQS).getServiceEndpoint();
        String region = localStackContainer.getEndpointConfiguration(LocalStackContainer.Service.SQS).getSigningRegion();

        ClientConfiguration clientConfiguration = getClientConfiguration(endpoint);

        return SQSConnectionFactory.builder()
                .withClientConfiguration(clientConfiguration)
                .withRegion(Region.getRegion(Regions.valueOf(region.toUpperCase().replaceAll("-","_"))))
                .withAWSCredentialsProvider(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    /**
     * Using SQS client to create a queue to avoid "QueueNotFound" from unit tests.
     */
    @Bean
    SqsClient getSqlClient() {
        SqsClient sqsClient = SqsClient.builder()
                .region(software.amazon.awssdk.regions.Region.US_EAST_1)
                .endpointOverride(URI.create(protocol + "://" + host + ":" + port))
                .build();
        sqsClient.createQueue(CreateQueueRequest.builder().queueName(createPersonQueue).build());
        return sqsClient;
    }

    private ClientConfiguration getClientConfiguration(String endpoint) {
        protocol = endpoint.substring(0, endpoint.indexOf(":"));
        host = endpoint.substring(endpoint.indexOf("//") + 2, endpoint.lastIndexOf(":"));
        port = Integer.parseInt(endpoint.substring(endpoint.lastIndexOf(":") + 1));

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.valueOf(protocol.toUpperCase()));
        clientConfiguration.setProxyHost(host);
        clientConfiguration.setProxyPort(port);

        return clientConfiguration;
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
