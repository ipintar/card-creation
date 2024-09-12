package com.task.client.card.app;

import com.task.client.card.app.controller.ClientController;
import com.task.client.card.app.dto.ClientDTO;
import com.task.client.card.app.dto.NewCardRequest;
import com.task.client.card.app.dto.Response;
import com.task.client.card.app.entity.Client;
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
import static org.mockito.Mockito.when;

public class ClientControllerTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

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
        clientDTO.setIme("Ana");
        clientDTO.setPrezime("Anić");
        clientDTO.setOib("12345678903");
        clientDTO.setStatusKartice("Accepted");

        Client client = ClientMapper.toEntity(clientDTO);

        when(clientRepository.save(client)).thenReturn(client);

        ResponseEntity<String> response = clientController.createClient(clientDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Klijent uspješno kreiran", response.getBody());
    }

    @Test
    void getClientByOibTest() {
        String oib = "12345678903";
        Client client = new Client();
        client.setIme("Ana");
        client.setPrezime("Anić");
        client.setOib(oib);
        client.setStatusKartice("Accepted");

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
        client.setIme("Ana");
        client.setPrezime("Anić");
        client.setOib(oib);
        client.setStatusKartice("Accepted");

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
        client.setIme("Ana");
        client.setPrezime("Anić");
        client.setOib(oib);
        client.setStatusKartice("Accepted");

        NewCardRequest newCardRequest = ClientMapper.toNewCardRequest(client);
        Response response = new Response();
        response.setMessage("New card request successfully created.");

        when(clientRepository.findByOib(oib)).thenReturn(client);
        when(restTemplate.postForEntity(apiUrl, newCardRequest, Response.class))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.CREATED));

        ResponseEntity<String> result = clientController.sendClientToApi(oib);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Podaci poslani na API, status: New card request successfully created.", result.getBody());
    }

    @Test
    void sendClientToApiClientNotFoundTest() {
        String oib = "12345678903";

        when(clientRepository.findByOib(oib)).thenReturn(null);

        ResponseEntity<String> result = clientController.sendClientToApi(oib);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Klijent nije pronađen", result.getBody());
    }

}
