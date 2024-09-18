package com.task.client.card.app.controller;

import com.task.client.card.app.dto.ClientDTO;
import com.task.client.card.app.dto.ErrorResponse;
import com.task.client.card.app.dto.NewCardRequest;
import com.task.client.card.app.dto.Response;
import com.task.client.card.app.entity.Client;
import com.task.client.card.app.exception.ExternalApiException;
import com.task.client.card.app.kafka.KafkaService;
import com.task.client.card.app.mapper.ClientMapper;
import com.task.client.card.app.repository.ClientRepository;
import com.task.client.card.app.service.EncryptionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Value("${api.url}")
    private String apiUrl;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private EncryptionService encryptionService;

    /**
     * Creates a new client from the provided data.
     *
     * @param clientDTO Data Transfer Object representing the client.
     * @return Response entity indicating whether the client was created successfully.
     */
    @PostMapping
    public ResponseEntity<String> createClient(final @Valid @RequestBody ClientDTO clientDTO) {
        logger.info("Received request to create client: {}", clientDTO);

        final Client client = ClientMapper.toClientEntity(clientDTO);

        clientRepository.save(client);

        logger.info("Client successfully created and saved to the database: {}", client);

        return new ResponseEntity<>("Client successfully created.", HttpStatus.CREATED);
    }

    /**
     * Retrieves a client by their OIB.
     *
     * @param oib The OIB of the client.
     * @return Response entity containing the client if found, or 404 if not found.
     */
    @GetMapping("/{oib}")
    public ResponseEntity<Client> getClientByOib(final @PathVariable String oib) throws Exception {
        logger.info("Received request to search client with OIB: {}", oib);

        final Client client = clientRepository.findByOib(encryptionService.encrypt(oib));
        if (client != null) {
            logger.info("Client found: {}", client);
            return new ResponseEntity<>(client, HttpStatus.OK);
        } else {
            logger.warn("Client with OIB {} not found", oib);
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
    public ResponseEntity<Void> deleteClientByOib(final @PathVariable String oib) throws Exception {
        logger.info("Received request to delete client with OIB: {}", oib);

        final Client client = clientRepository.findByOib(encryptionService.encrypt(oib));
        if (client != null) {
            clientRepository.deleteByOib(oib);
            logger.info("Client with OIB {} successfully deleted", oib);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("Client with OIB {} not found, deletion not possible", oib);
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
    public ResponseEntity<String> sendClientToApi(final @PathVariable String oib) throws Exception {
        logger.info("Received request to send client data with OIB {} to API", oib);
        final Client client = clientRepository.findByOib(encryptionService.encrypt(oib));
        if (client == null) {
            logger.warn("Client with OIB {} not found", oib);
            return ResponseEntity.status(404).body("Client not found");
        }
        final NewCardRequest newCardRequest = ClientMapper.toNewCardRequestDto(client);
        try {
            final ResponseEntity<Response> response = restTemplate.postForEntity(apiUrl, newCardRequest, Response.class);
            logger.info("Data successfully sent to API, status: {}", response.getStatusCode());

            kafkaService.sendAsync("API response for OIB: " + oib + " -> "
                    + response.getBody().getMessage()
                    + " @ " + LocalDateTime.now());

            return ResponseEntity.ok("Data sent to API, status: " + response.getBody().getMessage());
        } catch (ExternalApiException e) {
            final ErrorResponse errorResponse = e.getErrorResponse();
            logger.error("Error while sending data to API: {} -> {}", errorResponse.getCode(), errorResponse.getDescription());

            kafkaService.sendAsync("Error while sending data to API for OIB: " + oib + " -> "
                    + errorResponse.getDescription());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + errorResponse.getDescription());
        } catch (Exception e) {
            logger.error("Error while sending data to API for OIB: {}", oib, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending to API.");
        }
    }

}
