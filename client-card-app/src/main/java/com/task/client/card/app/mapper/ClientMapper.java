package com.task.client.card.app.mapper;

import com.task.client.card.app.entity.Client;
import com.task.client.card.app.dto.ClientDTO;
import com.task.client.card.app.dto.NewCardRequest;

/**
 * Utility class for mapping between ClientDTO and Client entities.
 */
public final class ClientMapper {

    private ClientMapper() {
    }

    /**
     * Converts ClientDTO to Client entity.
     *
     * @param clientDTO the client DTO to convert
     * @return the corresponding Client entity
     */
    public static Client toEntity(final ClientDTO clientDTO) {
        final Client client = new Client();
        client.setIme(clientDTO.getIme());
        client.setPrezime(clientDTO.getPrezime());
        client.setOib(clientDTO.getOib());
        client.setStatusKartice(clientDTO.getStatusKartice());
        return client;
    }

    /**
     * Converts Client entity to NewCardRequest DTO.
     *
     * @param client the client entity to convert
     * @return the corresponding NewCardRequest DTO
     */
    public static NewCardRequest toNewCardRequest(final Client client) {
        final NewCardRequest newCardRequest = new NewCardRequest();
        newCardRequest.setFirstName(client.getIme());
        newCardRequest.setLastName(client.getPrezime());
        newCardRequest.setOib(client.getOib());
        newCardRequest.setStatus(client.getStatusKartice());
        return newCardRequest;
    }
}

