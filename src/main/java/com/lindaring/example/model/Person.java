package com.lindaring.example.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {
    private String firstName;
    private String lastName;

    public static Person fromJSON(String json) {
        return new GsonBuilder().create().fromJson(json, Person.class);
    }

    public String toJSON(Person person) {
        return new Gson().toJson(person);
    }
}
