package ar.com.plug.examen.app.rest;

import ar.com.plug.examen.app.api.ClientApi;
import ar.com.plug.examen.domain.model.ClientResponse;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ShoppingCartResponse;
import ar.com.plug.examen.domain.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClientController handles client related operations.
 */
@RestController
@RequestMapping(path = "/client")
public class ClientController {

    private Logger logger = LoggerFactory.getLogger(ClientController.class);
    @Autowired
    private ClientService clientService;

    /**
     * Add client given as a parameter.
     * If client dni already exists, returns HTTP 500 with error message.
     *
     * @param clientRequest client to be added.
     * @return client newly added.
     */
    @PostMapping(path = "/add",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addClient(@RequestBody ClientApi clientRequest) {
        logger.info("Add client : {}", clientRequest.toString());
        try {
            ClientResponse clientResponse = clientService.addClient(clientRequest);
            logger.info("Client {}, {} saved successfully.", clientResponse.getName(), clientResponse.getSurname());
            return new ResponseEntity<>(clientResponse, HttpStatus.CREATED);
        } catch (PaymentException e) {
            logger.error("Exception when trying to add client - {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete client according to DNI.
     * If client DNI is not found, returns HTTP 404 with error message.
     *
     * @param clientDni client DNI to be removed.
     * @return HTTP 200 - ok.
     */
    @DeleteMapping(path = "/remove/{clientDni}")
    public ResponseEntity<?> removeClientByDni(@PathVariable int clientDni) {
        try {
            logger.info("Remove client with DNI {}.", clientDni);
            clientService.removeClientByDni(clientDni);
        } catch (PaymentException e) {
            logger.error("Exception when trying to delete client : {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        logger.info("Client with DNI {} deleted successfully.", clientDni);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update user by DNI.
     * If DNI is not found, returns HTTP 404 with error message.
     *
     * @param clientApi client to be updated.
     * @return recently updated client.
     */
    @PutMapping(path = "/update",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateClientByDni(@RequestBody ClientApi clientApi) {
        try {
            logger.info("Update client with DNI {}", clientApi.getDni());
            ClientResponse clientResponse = clientService.updateClient(clientApi);
            logger.info("Client with DNI {} was updated successfully.", clientResponse.getDni());
            return new ResponseEntity<>(clientResponse, HttpStatus.OK);
        } catch (PaymentException e) {
            logger.error("Exception when trying to update client : {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves the orders by a given DNI.
     * If DNI is not found, returns HTTP 500 with error message.
     *
     * @param clientDni DNI of client.
     * @return list of orders by given DNI.
     */
    @GetMapping(path = "/getOrders/{clientDni}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> getOrdersByDni(@PathVariable int clientDni) {
        try {
            logger.info("Retrieving orders for client with DNI {}", clientDni);
            List<ShoppingCartResponse> response = clientService.getOrdersByDni(clientDni);
            logger.info("Orders retrieved successfully : {}", response.toString());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PaymentException e) {
            logger.error("Exception when retrieving orders by DNI - {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
