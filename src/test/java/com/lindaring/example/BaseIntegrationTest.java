package com.lindaring.example;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.localstack.LocalStackContainer;

@ContextConfiguration(initializers = BaseIntegrationTest.Initializer.class)
public abstract class BaseIntegrationTest {
    public static LocalStackContainer localStackContainer = new LocalStackContainer()
            .withServices(LocalStackContainer.Service.SQS);

    static {
        localStackContainer.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "sqs.url=" + localStackContainer.getEndpointConfiguration(LocalStackContainer.Service.SQS)
                    .getServiceEndpoint()
            );
            values.applyTo(configurableApplicationContext);
        }
    }
}
