package com.task.client.card.app.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for card status updates.
 */
@Service
public class CardStatusConsumer {

    @KafkaListener(topics = "card-status-topic", groupId = "card-group")
    public void consume(final String message) {
        System.out.println("Received card status update: " + message);
    }
}
