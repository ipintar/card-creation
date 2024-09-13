package com.task.client.card.app.dto;

import lombok.Data;

/**
 * Class representing error responses for API requests.
 */
@Data
public class ErrorResponse {
    private String code;
    private String id;
    private String description;
}
