package com.task.client.card.app.exception;

import com.task.client.card.app.dto.ErrorResponse;
import lombok.Getter;

/**
 * Custom exception to handle API request errors.
 */
@Getter
public class ExternalApiException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public ExternalApiException(final String message, final ErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }

}

