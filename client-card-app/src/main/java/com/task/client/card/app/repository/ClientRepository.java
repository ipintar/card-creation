package com.task.client.card.app.repository;

import com.task.client.card.app.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing client data from the database.
 */
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByOib(String oib);

    void deleteByOib(String oib);
}
