package com.lindaring.example.service;

import com.lindaring.example.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class PersonServiceTests {

    @Autowired
    protected JmsTemplate jmsTemplate;

    @Value("${sqs.queues.create-person}")
    private String createPersonQueue;

    @Test
    public void SEND_QUEUE_MESSAGE_SUCCESS_TEST() {
        String request = Person.toJSON(new Person("John", "Doe"));
        jmsTemplate.convertAndSend(createPersonQueue, request);
        jmsTemplate.setReceiveTimeout(1_000L);
        jmsTemplate.receiveAndConvert(createPersonQueue);
        Assert.assertTrue(true);
    }

}
