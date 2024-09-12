package com.task.client.card.app.controller;

import com.task.client.card.app.dto.ClientDTO;
import com.task.client.card.app.dto.NewCardRequest;
import com.task.client.card.app.dto.Response;
import com.task.client.card.app.entity.Client;
import com.task.client.card.app.mapper.ClientMapper;
import com.task.client.card.app.repository.ClientRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * REST controller for managing client-related operations.
 */
@RestController
@RequestMapping("/clients")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private static final String TOPIC = "card-status-topic";

    @Value("${api.url}")
    private String apiUrl;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Creates a new client from the provided data.
     *
     * @param clientDTO Data Transfer Object representing the client.
     * @return Response entity indicating whether the client was created successfully.
     */
    @PostMapping
    public ResponseEntity<String> createClient(final @Valid @RequestBody ClientDTO clientDTO) {
        logger.info("Primljen zahtjev za kreiranje klijenta: {}", clientDTO);

        final Client client = ClientMapper.toEntity(clientDTO);

        clientRepository.save(client);

        logger.info("Klijent uspješno kreiran i spremljen u bazu: {}", client);

        return new ResponseEntity<>("Klijent uspješno kreiran", HttpStatus.CREATED);
    }

    /**
     * Retrieves a client by their OIB.
     *
     * @param oib The OIB of the client.
     * @return Response entity containing the client if found, or 404 if not found.
     */
    @GetMapping("/{oib}")
    public ResponseEntity<Client> getClientByOib(final @PathVariable String oib) {
        logger.info("Primljen zahtjev za pretragu klijenta s OIB-om: {}", oib);

        final Client client = clientRepository.findByOib(oib);
        if (client != null) {
            logger.info("Klijent pronađen: {}", client);
            return new ResponseEntity<>(client, HttpStatus.OK);
        } else {
            logger.warn("Klijent s OIB-om {} nije pronađen", oib);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a client by their OIB.
     *
     * @param oib The OIB of the client to be deleted.
     * @return Response entity indicating success or failure of deletion.
     */
    @Transactional
    @DeleteMapping("/{oib}")
    public ResponseEntity<Void> deleteClientByOib(final @PathVariable String oib) {
        logger.info("Primljen zahtjev za brisanje klijenta s OIB-om: {}", oib);

        final Client client = clientRepository.findByOib(oib);
        if (client != null) {
            clientRepository.deleteByOib(oib);
            logger.info("Klijent s OIB-om {} uspješno obrisan", oib);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("Klijent s OIB-om {} nije pronađen, brisanje nije moguće", oib);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Sends client data to an external API.
     *
     * @param oib The OIB of the client whose data is being sent.
     * @return Response entity indicating the result of the operation.
     */
    @PostMapping("/send/{oib}")
    public ResponseEntity<String> sendClientToApi(final @PathVariable String oib) {
        logger.info("Primljen zahtjev za slanje podataka klijenta s OIB-om {} na API", oib);

        final Client client = clientRepository.findByOib(oib);
        if (client == null) {
            logger.warn("Klijent s OIB-om {} nije pronađen", oib);
            return ResponseEntity.status(404).body("Klijent nije pronađen");
        }
        final NewCardRequest newCardRequest = ClientMapper.toNewCardRequest(client);
        try {
            final ResponseEntity<Response> response = restTemplate.postForEntity(apiUrl, newCardRequest, Response.class);
            logger.info("Podaci uspješno poslani na API, status: {}", response.getStatusCode());

            kafkaTemplate.send(TOPIC, "API odgovor za OIB: " + oib + " -> "
                    + response.getBody().getMessage()
                    + " @ " + LocalDateTime.now());

            return ResponseEntity.ok("Podaci poslani na API, status: " + response.getBody().getMessage());
        } catch (Exception e) {
            logger.error("Greška pri slanju podataka na API za OIB: {}", oib, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greška pri slanju na API.");
        }
    }

}
