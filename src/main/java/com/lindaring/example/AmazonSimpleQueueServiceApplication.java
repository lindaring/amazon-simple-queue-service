package com.lindaring.example;

import io.smartup.localstack.EnableLocalStack;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableLocalStack
@SpringBootApplication
public class AmazonSimpleQueueServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmazonSimpleQueueServiceApplication.class, args);
	}

}
