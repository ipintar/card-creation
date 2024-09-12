package com.task.client_card_app.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CardStatusConsumer {

    @KafkaListener(topics = "card-status-topic", groupId = "card-group")
    public void consume(String message) {
        System.out.println("Received card status update: " + message);
    }
}
