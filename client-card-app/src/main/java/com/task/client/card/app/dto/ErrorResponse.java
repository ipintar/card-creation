package com.task.client.card.app.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String code;
    private String id;
    private String description;
}
