package com.task.client.card.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication
public class ClientCardAppApplication {

	public static void main(final String[] args) {
		SpringApplication.run(ClientCardAppApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
