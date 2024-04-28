package com.learn.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class DictionaryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DictionaryServiceApplication.class, args);
	}

}
