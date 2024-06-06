package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VonageApplication {

	public static void main(String[] args) {
		SpringApplication.run(VonageApplication.class, args);
		System.out.println("Hello, Vonage!");
	}

}
