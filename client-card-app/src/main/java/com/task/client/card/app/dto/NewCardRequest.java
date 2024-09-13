package com.task.client.card.app.dto;

import com.task.client.card.app.enums.CardStatus;
import lombok.Data;

/**
 * Class representing the request body for creating a new card.
 */
@Data
public class NewCardRequest {

    private String firstName;
    private String lastName;
    private CardStatus status;
    private String oib;

}
