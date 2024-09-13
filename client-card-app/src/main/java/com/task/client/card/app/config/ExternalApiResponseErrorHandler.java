package com.task.client.card.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.client.card.app.dto.ErrorResponse;
import com.task.client.card.app.exception.ExternalApiException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * Custom error handler for handling API response errors.
 */
public class ExternalApiResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        final ErrorResponse errorResponse = objectMapper.readValue(response.getBody(), ErrorResponse.class);

        throw new ExternalApiException("API request failed: " + errorResponse.getDescription(), errorResponse);
    }
}

