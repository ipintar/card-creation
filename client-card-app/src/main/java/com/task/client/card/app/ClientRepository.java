package com.task.client.card.app;

import com.task.client.card.app.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByOib(String oib);
    void deleteByOib(String oib);
}
