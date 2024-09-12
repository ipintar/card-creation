package com.task.client.card.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientDTO {

    @NotNull
    @Size(min = 2, max = 50, message = "Ime mora imati između 2 i 50 znakova.")
    private String ime;

    @NotNull
    @Size(min = 2, max = 50, message = "Prezime mora imati između 2 i 50 znakova.")
    private String prezime;

    @NotNull
    @Pattern(regexp = "\\d{11}", message = "OIB mora imati točno 11 znamenki.")
    private String oib;

    @NotNull
    @Size(min = 3, max = 20, message = "Status kartice mora imati između 3 i 20 znakova.")
    private String statusKartice;
}

