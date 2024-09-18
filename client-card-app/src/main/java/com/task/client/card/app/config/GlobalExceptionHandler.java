package com.task.client.card.app.config;

import com.task.client.card.app.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

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
    public ResponseEntity<ErrorResponse> handleHttpClientError(final HttpClientErrorException ex, final WebRequest request) {
        logger.error("Global exception handler caught client error: {}", ex.getMessage());
        final ErrorResponse errorResponse = new ErrorResponse();
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
    public ResponseEntity<ErrorResponse> handleHttpServerError(final HttpServerErrorException ex, final WebRequest request) {
        logger.error("Global exception handler caught server error: {}", ex.getMessage());
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(String.valueOf(ex.getStatusCode().value()));
        errorResponse.setDescription("Global handler: Server error: " + ex.getStatusText());

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    /**
     * Handles validation errors that occur when a method argument annotated with {@code @Valid} fails validation.
     * Collects all the field errors and constructs a detailed error message listing all validation issues.
     *
     * @param ex the {@link MethodArgumentNotValidException} containing validation errors.
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} with the validation errors and a 400 BAD REQUEST status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(final MethodArgumentNotValidException ex) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        final StringBuilder errorMessageBuilder = new StringBuilder("Global handler: Validation errors:");

        for (FieldError error : fieldErrors) {
            errorMessageBuilder.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append(";");
        }

        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(HttpStatus.BAD_REQUEST.toString());
        errorResponse.setDescription(errorMessageBuilder.toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles errors that occur when an incoming HTTP request body cannot be deserialized into the target object.
     * Typically occurs when an invalid input is sent (e.g., wrong enum values, missing fields, etc.).
     * Logs the error and returns a message with the specific deserialization issue.
     *
     * @param ex      the {@link HttpMessageNotReadableException} containing the deserialization error details.
     * @param request the current web request.
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} with the deserialization error and a 400 BAD REQUEST status.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse>
    handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex, final WebRequest request) {
        logger.error("Error deserializing request body: {}", ex.getMessage());

        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(HttpStatus.BAD_REQUEST.toString());
        errorResponse.setDescription("Global Handler: Invalid input: " + ex.getMostSpecificCause().getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<ErrorResponse> handleGlobalException(final Exception ex, final WebRequest request) {
        logger.error("Global exception handler caught an unexpected error: {} - Stacktrace: {}", ex.getMessage(), ex);
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorResponse.setDescription("Global handler: An unexpected error occurred. -> " + ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
