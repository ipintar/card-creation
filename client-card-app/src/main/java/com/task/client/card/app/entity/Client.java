package com.task.client.card.app.entity;

import com.task.client.card.app.enums.CardStatus;
import com.task.client.card.app.service.EncryptionService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Entity class representing a client in the database.
 */
@Entity
@Data
@Component
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
    //@Pattern(regexp = "\\d{11}", message = "OIB must be exactly 11 digits.")
    @Column(unique = true)
    private String oib;

    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;


    @PrePersist
    @PreUpdate
    private void encryptFields() throws Exception {
        final EncryptionService encryptionService = new EncryptionService();
        this.firstName = encryptionService.encrypt(this.firstName);
        this.lastName = encryptionService.encrypt(this.lastName);
        this.oib = encryptionService.encrypt(this.oib);
    }

    @PostLoad
    private void decryptFields() throws Exception {
        final EncryptionService encryptionService = new EncryptionService();
        this.firstName = encryptionService.decrypt(this.firstName);
        this.lastName = encryptionService.decrypt(this.lastName);
        this.oib = encryptionService.decrypt(this.oib);
    }
}
