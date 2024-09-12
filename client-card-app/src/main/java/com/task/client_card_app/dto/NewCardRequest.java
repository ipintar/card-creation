package com.task.client_card_app.dto;

import lombok.Data;

@Data
public class NewCardRequest {

    private String firstName;
    private String lastName;
    private String status;
    private String oib;

}
