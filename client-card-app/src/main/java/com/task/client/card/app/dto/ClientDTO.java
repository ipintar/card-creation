package com.task.client.card.app.dto;

import com.task.client.card.app.enums.CardStatus;
import com.task.client.card.app.validation.OibValidator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object representing the client.
 */
@Data
public class ClientDTO {

    @NotNull
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String firstName;

    @NotNull
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    private String lastName;

    @OibValidator
    private String oib;

    @NotNull
    private CardStatus cardStatus;

}

