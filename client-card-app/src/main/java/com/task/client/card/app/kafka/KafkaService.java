package com.task.client.card.app.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for card status updates.
 */
@Service
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "card-status-topic";

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void sendAsync(String message) {
        kafkaTemplate.send(TOPIC, message);
        // You could also log the result asynchronously
        System.out.println("Received card status update: " + message);
    }
}