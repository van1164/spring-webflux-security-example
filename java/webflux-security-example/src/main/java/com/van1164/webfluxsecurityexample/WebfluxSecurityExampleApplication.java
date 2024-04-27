package com.van1164.webfluxsecurityexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class WebfluxSecurityExampleApplication {

	public static void main(String[] args) {

		SpringApplication.run(WebfluxSecurityExampleApplication.class, args);
	}

}
