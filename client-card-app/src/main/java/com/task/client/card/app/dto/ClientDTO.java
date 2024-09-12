package com.task.client.card.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object representing the client.
 */
@Data
public class ClientDTO {

    @NotNull
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String ime;

    @NotNull
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    private String prezime;

    @NotNull
    @Pattern(regexp = "\\d{11}", message = "OIB must be exactly 11 digits.")
    private String oib;

    @NotNull
    @Size(min = 3, max = 20, message = "Card status must be between 3 and 20 characters.")
    private String statusKartice;

}

