package com.lindaring.example;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.localstack.LocalStackContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AutoStartLocalStackTest {

    @Before
    public void setup() {
        LocalStackContainer localStackContainer = new LocalStackContainer()
                .withServices(LocalStackContainer.Service.SQS);
        localStackContainer.start();
    }

    @Test
    public void FIRST_TEST() {
        assertTrue(true);
    }

}
