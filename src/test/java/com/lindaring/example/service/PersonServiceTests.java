package com.lindaring.example.service;

import com.lindaring.example.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class PersonServiceTests {

    @Autowired
    private PersonService personService;

    @Test
    public void CAN_SEND_QUEUE_MESSAGE() {
        personService.initPersonCreation(new Person("John", "Doe"));
    }

}
