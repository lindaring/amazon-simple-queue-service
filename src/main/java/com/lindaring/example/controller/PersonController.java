package com.lindaring.example.controller;

import com.lindaring.example.model.Person;
import com.lindaring.example.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping
    public boolean createPerson(@RequestBody Person person) {
        personService.initPersonCreation(person);
        return true;
    }

}
