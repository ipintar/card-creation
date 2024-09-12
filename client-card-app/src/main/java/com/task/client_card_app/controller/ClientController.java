package com.task.client_card_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.client_card_app.dto.ErrorResponse;
import com.task.client_card_app.dto.Response;
import com.task.client_card_app.entity.Client;
import com.task.client_card_app.ClientRepository;
import com.task.client_card_app.dto.ClientDTO;
import com.task.client_card_app.dto.NewCardRequest;
import com.task.client_card_app.mapper.ClientMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/clients")
public class ClientController {

    @Value("${api.url}")
    private String apiUrl;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private static final String TOPIC = "card-status-topic";

    @PostMapping
    public ResponseEntity<String> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        logger.info("Primljen zahtjev za kreiranje klijenta: {}", clientDTO);

        Client client = ClientMapper.toEntity(clientDTO);

        clientRepository.save(client);

        logger.info("Klijent uspješno kreiran i spremljen u bazu: {}", client);

        return new ResponseEntity<>("Klijent uspješno kreiran", HttpStatus.CREATED);
    }

    @GetMapping("/{oib}")
    public ResponseEntity<Client> getClientByOib(@PathVariable String oib) {
        logger.info("Primljen zahtjev za pretragu klijenta s OIB-om: {}", oib);

        Client client = clientRepository.findByOib(oib);
        if (client != null) {
            logger.info("Klijent pronađen: {}", client);
            return new ResponseEntity<>(client, HttpStatus.OK);
        } else {
            logger.warn("Klijent s OIB-om {} nije pronađen", oib);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @DeleteMapping("/{oib}")
    public ResponseEntity<Void> deleteClientByOib(@PathVariable String oib) {
        logger.info("Primljen zahtjev za brisanje klijenta s OIB-om: {}", oib);

        Client client = clientRepository.findByOib(oib);
        if (client != null) {
            clientRepository.deleteByOib(oib);
            logger.info("Klijent s OIB-om {} uspješno obrisan", oib);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("Klijent s OIB-om {} nije pronađen, brisanje nije moguće", oib);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/send/{oib}")
    public ResponseEntity<String> sendClientToApi(@PathVariable String oib) {
        logger.info("Primljen zahtjev za slanje podataka klijenta s OIB-om {} na API", oib);

        Client client = clientRepository.findByOib(oib);
        if (client == null) {
            logger.warn("Klijent s OIB-om {} nije pronađen", oib);
            return ResponseEntity.status(404).body("Klijent nije pronađen");
        }

        NewCardRequest newCardRequest = ClientMapper.toNewCardRequest(client);

        try {
            ResponseEntity<Response> response = restTemplate.postForEntity(apiUrl, newCardRequest, Response.class);
            logger.info("Podaci uspješno poslani na API, status: {}", response.getStatusCode());

            kafkaTemplate.send(TOPIC, "API odgovor za OIB: " + oib + " -> " + response.getBody().getMessage());

            return ResponseEntity.ok("Podaci poslani na API, status: " + response.getBody().getMessage());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ErrorResponse errorResponse = mapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
                logger.error("Greška pri slanju podataka na API: {} -> {}", errorResponse.getCode(), errorResponse.getDescription());
                return ResponseEntity.status(e.getStatusCode()).body("Greška: " + errorResponse.getDescription());
            } catch (Exception parseException) {
                logger.error("Greška pri parsiranju ErrorResponse: {}", parseException.getMessage());
                return ResponseEntity.status(e.getStatusCode()).body("Greška pri slanju na API.");
            }
        } catch (Exception e) {
            logger.error("Greška pri slanju podataka na API za OIB: {}", oib, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greška pri slanju na API.");
        }
    }

}
