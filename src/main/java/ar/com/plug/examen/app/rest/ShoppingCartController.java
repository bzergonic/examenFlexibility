package ar.com.plug.examen.app.rest;

import ar.com.plug.examen.app.api.ShoppingCartApi;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ShoppingCartResponse;
import ar.com.plug.examen.domain.service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ShoppingCartController handles Shopping Cart related operations.
 */
@RestController
@RequestMapping(path = "/shopping")
public class ShoppingCartController {

    private Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * Creates order with given products for a given client (DNI).
     * If client not found, returns HTTP 500 with error message.
     *
     * @param shoppingCartApi order to be created for given client.
     * @return order created with the total order amount.
     */
    @PostMapping(path = "/createOrder",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrder(@RequestBody ShoppingCartApi shoppingCartApi) {
        try {
            logger.info("Create order - {}", shoppingCartApi.toString());
            ShoppingCartResponse response = shoppingCartService.createOrder(shoppingCartApi);
            logger.info("Order created successfully with order ID {}", response.getOrderId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (PaymentException e) {
            logger.error("Exception when trying to create order - {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Approves the order for given order id.
     * If order is not found, returns HTTP 500 with error message.
     *
     * @param orderId order to be approved.
     * @return updated order.
     */
    @PutMapping(path = "/approveOrder/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> approveOrder(@PathVariable long orderId) {
        try {
            logger.info("Approving orderId {}", orderId);
            ShoppingCartResponse response = shoppingCartService.approveOrder(orderId);
            logger.info("OrderId {} successfully approved.", orderId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PaymentException e) {
            logger.error("Exception when trying to approve order - {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Pay the order for given order id.
     * If order is not found, returns HTTP 500 with error message.
     *
     * @param orderId order to be paid.
     * @return updated order.
     */
    @PutMapping(path = "/payOrder/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> payOrder(@PathVariable long orderId) {
        try {
            logger.info("Paying orderId {}", orderId);
            ShoppingCartResponse response = shoppingCartService.payOrder(orderId);
            logger.info("OrderId {} successfully paid.", orderId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PaymentException e) {
            logger.error("Exception when trying to pay order - {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
