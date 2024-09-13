package com.task.client.card.app.mapper;

import com.task.client.card.app.dto.ClientDTO;
import com.task.client.card.app.dto.NewCardRequest;
import com.task.client.card.app.entity.Client;

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
    public static Client toClientEntity(final ClientDTO clientDTO) {
        final Client client = new Client();
        client.setFirstName(clientDTO.getFirstName());
        client.setLastName(clientDTO.getLastName());
        client.setOib(clientDTO.getOib());
        client.setCardStatus(clientDTO.getCardStatus());
        return client;
    }

    /**
     * Converts Client entity to NewCardRequest DTO.
     *
     * @param client the client entity to convert
     * @return the corresponding NewCardRequest DTO
     */
    public static NewCardRequest toNewCardRequestDto(final Client client) {
        final NewCardRequest newCardRequest = new NewCardRequest();
        newCardRequest.setFirstName(client.getFirstName());
        newCardRequest.setLastName(client.getLastName());
        newCardRequest.setOib(client.getOib());
        newCardRequest.setStatus(client.getCardStatus());
        return newCardRequest;
    }
}

