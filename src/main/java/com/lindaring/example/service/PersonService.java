package com.lindaring.example.service;

import com.google.gson.JsonSyntaxException;
import com.lindaring.example.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PersonService {

    @Value("${sqs.queues.create-person}")
    private String createPersonQueue;

    @Autowired
    protected JmsTemplate jmsTemplate;

    public void initPersonCreation(Person person) {
        log.info("Initializing create person...");
        jmsTemplate.convertAndSend(createPersonQueue, Person.toJSON(person));
    }

    @JmsListener(destination = "${sqs.queues.create-person}")
    public void createPerson(String request) {
        log.info("Receiving create person request...\n" + request);
        try {
            Person person = Person.fromJSON(request);
            log.info(String.format("Person [%s] has been created.", person.getFirstName()));
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse JSON request.");
        }
    }

}
