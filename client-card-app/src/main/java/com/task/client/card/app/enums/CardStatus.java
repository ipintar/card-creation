package com.task.client.card.app.enums;


/**
 * Enum representing the various statuses a card can have.
 */
public enum CardStatus {
    ACCEPTED,
    PENDING,
    REJECTED,
    ACTIVE,
    EXPIRED;

    @Override
    public String toString() {
        return name().toUpperCase();
    }
}
