package com.lindaring.example.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.lindaring.example.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class PersonService {

    public static final String CREATE_PERSON_REQUESTS = "create_person_queue";

    @Autowired
    protected JmsTemplate defaultJmsTemplate;

    public void initPersonCreation(Person person) {
        log.info("Initializing create person...");
        defaultJmsTemplate.convertAndSend(CREATE_PERSON_REQUESTS, person.toJSON(person));
    }

    @JmsListener(destination = CREATE_PERSON_REQUESTS)
    public void createPerson(String request) {
        log.info("Receiving create person request..." + request);
        try {
            Person person = Person.fromJSON(request);
            log.info(String.format("Person [%s] has been created.", person.getFirstName()));
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse JSON request.");
        }
    }

}
