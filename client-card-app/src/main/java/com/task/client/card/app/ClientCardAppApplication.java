package com.task.client.card.app;

import com.task.client.card.app.config.ExternalApiResponseErrorHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

/**
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication
@EnableAsync
public class ClientCardAppApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ClientCardAppApplication.class, args);
    }

	   /**
	 * Bean definition for {@link RestTemplate}, which is used for making HTTP requests
	 * to external APIs. This method also sets a custom error handler to process
	 * error responses from the external API.
	 *
	 * @return a configured instance of {@link RestTemplate} with {@link ExternalApiResponseErrorHandler}
	 *
	 * */
    @Bean
    public RestTemplate restTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ExternalApiResponseErrorHandler());
        return restTemplate;
    }
}
