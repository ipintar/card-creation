package com.task.client.card.app.config;

import com.task.client.card.app.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * GlobalExceptionHandler handles exceptions globally for the application.
 * It intercepts client, server, and other general exceptions, and returns a structured
 * {@link ErrorResponse} with relevant HTTP status codes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles HTTP client errors (4xx).
     * Logs the error and returns an appropriate {@link ErrorResponse}.
     *
     * @param ex      the thrown {@link HttpClientErrorException}
     * @param request the current web request
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} and the relevant HTTP status
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientError(HttpClientErrorException ex, WebRequest request) {
        logger.error("Global exception handler caught client error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(String.valueOf(ex.getStatusCode().value()));
        errorResponse.setDescription("Global handler: Client error: " + ex.getStatusText());

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    /**
     * Handles HTTP server errors (5xx).
     * Logs the error and returns an appropriate {@link ErrorResponse}.
     *
     * @param ex      the thrown {@link HttpServerErrorException}
     * @param request the current web request
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} and the relevant HTTP status
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerError(HttpServerErrorException ex, WebRequest request) {
        logger.error("Global exception handler caught server error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(String.valueOf(ex.getStatusCode().value()));
        errorResponse.setDescription("Global handler: Server error: " + ex.getStatusText());

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    /**
     * Handles general exceptions.
     * Logs the error and returns a generic {@link ErrorResponse}.
     *
     * @param ex      the thrown {@link Exception}
     * @param request the current web request
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} and a 500 INTERNAL SERVER ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Global exception handler caught an unexpected error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorResponse.setDescription("Global handler: An unexpected error occurred.");

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
