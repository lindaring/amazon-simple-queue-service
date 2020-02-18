package com.lindaring.example.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
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
