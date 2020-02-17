package com.lindaring.example.controller;

import com.lindaring.example.service.SQSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SQSController {

    @Autowired
    private SQSService sqsService;

    @PostMapping
    public void sendMessage(@RequestBody String notificationData) {
        sqsService.sendQueueMessage(notificationData);
    }

    @GetMapping
    public String readMessage() {
        return sqsService.readQueueMessage();
    }

}
