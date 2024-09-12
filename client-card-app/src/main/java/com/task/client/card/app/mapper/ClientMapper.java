package com.task.client.card.app.mapper;

import com.task.client.card.app.entity.Client;
import com.task.client.card.app.dto.ClientDTO;
import com.task.client.card.app.dto.NewCardRequest;

public class ClientMapper {

    public static Client toEntity(ClientDTO clientDTO) {
        Client client = new Client();
        client.setIme(clientDTO.getIme());
        client.setPrezime(clientDTO.getPrezime());
        client.setOib(clientDTO.getOib());
        client.setStatusKartice(clientDTO.getStatusKartice());
        return client;
    }

    public static NewCardRequest toNewCardRequest(Client client) {
        NewCardRequest newCardRequest = new NewCardRequest();
        newCardRequest.setFirstName(client.getIme());
        newCardRequest.setLastName(client.getPrezime());
        newCardRequest.setOib(client.getOib());
        newCardRequest.setStatus(client.getStatusKartice());
        return newCardRequest;
    }
}

