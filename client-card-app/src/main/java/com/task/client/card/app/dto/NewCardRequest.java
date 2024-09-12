package com.task.client.card.app.dto;

import lombok.Data;

@Data
public class NewCardRequest {

    private String firstName;
    private String lastName;
    private String status;
    private String oib;

}
