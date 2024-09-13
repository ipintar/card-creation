package com.task.client.card.app.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for card status updates.
 */
@Service
public class KafkaService {

    private static final String TOPIC = "card-status-topic";
    private final KafkaTemplate<String, String> kafkaTemplate;


    public KafkaService(final KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a message asynchronously to the specified Kafka topic.
     * This method uses Spring's @Async annotation to enable asynchronous execution.
     * The method will immediately return, and the message will be sent in the background
     * to the Kafka topic defined by {@code TOPIC}.
     *
     * @param message the message to be sent to the Kafka topic.
     */
    @Async
    public void sendAsync(final String message) {
        kafkaTemplate.send(TOPIC, message);
        System.out.println("Received card status update: " + message);
    }
}