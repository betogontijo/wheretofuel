package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WhereToFuelApplication {

	@Autowired
	WebServiceController controller;

	public static void main(String[] args) {
		SpringApplication.run(WhereToFuelApplication.class, args);
	}
}
