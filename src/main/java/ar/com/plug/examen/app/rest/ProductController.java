package ar.com.plug.examen.app.rest;

import ar.com.plug.examen.app.api.ProductApi;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ProductResponse;
import ar.com.plug.examen.domain.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController handles product related operations.
 */
@RestController
@RequestMapping(path = "/product")
public class ProductController {

    private Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService productService;

    /**
     * Add new product.
     * If product name exists, returns HTTP 500 with error message.
     *
     * @param productRequest product to be added.
     * @return product newly added.
     */
    @PostMapping(path = "/add",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProduct(@RequestBody ProductApi productRequest) {
        try {
            logger.info("Add product {}", productRequest.toString());
            ProductResponse productResponse = productService.addProduct(productRequest);
            logger.info("Product {} saved successfully.", productResponse.getId());
            return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
        } catch (PaymentException e) {
            logger.error("Exception when trying to add product - {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete product according to id.
     * If product id is not found, returns HTTP 404 with error message.
     *
     * @param productId product id to be removed.
     * @return HTTP 200 - ok.
     */
    @DeleteMapping(path = "/remove/{productId}")
    public ResponseEntity<?> removeProductById(@PathVariable String productId) {
        try {
            logger.info("About to delete product {}", productId);
            productService.removeProductById(productId);
        } catch (PaymentException e) {
            logger.error("Exception when trying to remove product : {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        logger.info("Product {} deleted successfully.", productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update product by ID.
     * If ID is not found, returns HTTP 404 with error message.
     *
     * @param productRequest product to be updated.
     * @return recently updated product.
     */
    @PutMapping(path = "/update",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProductById(@RequestBody ProductApi productRequest) {
        try {
            logger.info("Update product {}.", productRequest.getId());
            ProductResponse productResponse = productService.updateProduct(productRequest);
            logger.info("Product {} was updated successfully.", productResponse.getId());
            return new ResponseEntity<>(productResponse, HttpStatus.OK);
        } catch (PaymentException e) {
            logger.error("Exception when trying to update product : {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * List all existing products.
     *
     * @return list with all existing products.
     */
    @GetMapping(path = "/listAll",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllProducts() {
        logger.info("Listing all products");
        List<ProductResponse> products = productService.getAllProducts();
        logger.info("Products listed successfully - {}", products.toString());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Retrieves single product by ID.
     *
     * @param productId product id to be retrieved.
     * @return product ID.
     */
    @GetMapping(path = "/find/{productId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductById(@PathVariable String productId) {
        logger.info("Search product ID {}", productId);
        ProductResponse productResponse = productService.findByProductId(productId);
        return productResponse != null ?
                new ResponseEntity<>(productResponse, HttpStatus.OK)
                : new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
    }
}
