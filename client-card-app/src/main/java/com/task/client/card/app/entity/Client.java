package com.task.client.card.app.entity;

import com.task.client.card.app.enums.CardStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Entity class representing a client in the database.
 */
@Entity
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50)
    private String firstName;

    @NotNull
    @Column(length = 50)
    private String lastName;

    @NotNull
    @Pattern(regexp = "\\d{11}", message = "OIB must be exactly 11 digits.")
    @Column(unique = true)
    private String oib;

    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;
}
