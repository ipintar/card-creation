package com.task.client.card.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.client.card.app.controller.ClientController;
import com.task.client.card.app.dto.ClientDTO;
import com.task.client.card.app.dto.ErrorResponse;
import com.task.client.card.app.dto.NewCardRequest;
import com.task.client.card.app.dto.Response;
import com.task.client.card.app.entity.Client;
import com.task.client.card.app.enums.CardStatus;
import com.task.client.card.app.exception.ExternalApiException;
import com.task.client.card.app.kafka.KafkaService;
import com.task.client.card.app.mapper.ClientMapper;
import com.task.client.card.app.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientControllerTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private ClientController clientController;

    @Value("${api.url}")
    private String apiUrl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createClientTest() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setFirstName("Ana");
        clientDTO.setLastName("Anić");
        clientDTO.setOib("12345678903");
        clientDTO.setCardStatus(CardStatus.ACCEPTED);

        Client client = ClientMapper.toClientEntity(clientDTO);

        when(clientRepository.save(client)).thenReturn(client);

        ResponseEntity<String> response = clientController.createClient(clientDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Client successfully created.", response.getBody());
    }

    @Test
    void getClientByOibTest() {
        String oib = "12345678903";
        Client client = new Client();
        client.setFirstName("Ana");
        client.setLastName("Anić");
        client.setOib(oib);
        client.setCardStatus(CardStatus.ACCEPTED);

        when(clientRepository.findByOib(oib)).thenReturn(client);

        ResponseEntity<Client> response = clientController.getClientByOib(oib);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(client, response.getBody());
    }

    @Test
    void getClientByOibNotFoundTest() {
        String oib = "12345678903";

        when(clientRepository.findByOib(oib)).thenReturn(null);

        ResponseEntity<Client> response = clientController.getClientByOib(oib);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteClientByOibTest() {
        String oib = "12345678903";
        Client client = new Client();
        client.setFirstName("Ana");
        client.setLastName("Anić");
        client.setOib(oib);
        client.setCardStatus(CardStatus.ACCEPTED);

        when(clientRepository.findByOib(oib)).thenReturn(client);

        ResponseEntity<Void> response = clientController.deleteClientByOib(oib);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteClientByOibNotFoundTest() {
        String oib = "12345678903";

        when(clientRepository.findByOib(oib)).thenReturn(null);

        ResponseEntity<Void> response = clientController.deleteClientByOib(oib);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void sendClientToApiTest() {
        String oib = "12345678903";
        Client client = new Client();
        client.setFirstName("Ana");
        client.setLastName("Anić");
        client.setOib(oib);
        client.setCardStatus(CardStatus.ACCEPTED);

        NewCardRequest newCardRequest = ClientMapper.toNewCardRequestDto(client);
        Response response = new Response();
        response.setMessage("New card request successfully created.");

        when(clientRepository.findByOib(oib)).thenReturn(client);
        when(restTemplate.postForEntity(apiUrl, newCardRequest, Response.class))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.CREATED));

        ResponseEntity<String> result = clientController.sendClientToApi(oib);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Data sent to API, status: New card request successfully created.", result.getBody());

        verify(kafkaService).sendAsync(anyString());
    }

    @Test
    void sendClientToApiExternalApiErrorTest() {
        String oib = "12345678903";
        Client client = new Client();
        client.setFirstName("Ana");
        client.setLastName("Anić");
        client.setOib(oib);
        client.setCardStatus(CardStatus.ACCEPTED);

        NewCardRequest newCardRequest = ClientMapper.toNewCardRequestDto(client);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("400");
        errorResponse.setDescription("Invalid data");

        when(clientRepository.findByOib(oib)).thenReturn(client);

        ExternalApiException apiException = new ExternalApiException("API error", errorResponse);
        when(restTemplate.postForEntity(apiUrl, newCardRequest, Response.class)).thenThrow(apiException);

        ResponseEntity<String> result = clientController.sendClientToApi(oib);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Error: Invalid data", result.getBody());

        verify(kafkaService).sendAsync(contains("Error while sending data to API"));
    }


    @Test
    void sendClientToApiClientNotFoundTest() {
        String oib = "12345678903";

        when(clientRepository.findByOib(oib)).thenReturn(null);

        ResponseEntity<String> result = clientController.sendClientToApi(oib);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Client not found", result.getBody());
    }


}
