package ar.com.plug.examen.domain.service.impl;

import ar.com.plug.examen.app.api.ProductApi;
import ar.com.plug.examen.domain.dao.ProductDAO;
import ar.com.plug.examen.domain.mapper.ProductMapper;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ProductResponse;
import ar.com.plug.examen.domain.repository.ProductRepository;
import ar.com.plug.examen.domain.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Product service operations.
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    /**
     * Save product into DB.
     *
     * @param productRq product to be saved in DB.
     * @return product added.
     * @throws PaymentException if product already exist.
     */
    @Override
    public ProductResponse addProduct(ProductApi productRq) throws PaymentException {
        if (productAlreadyExist(productRq)) {
            throw new PaymentException("Product " + productRq.getName() + " already exist.");
        }
        ProductDAO savedProductDAO = productRepository.saveAndFlush(
                productMapper.mapProductDTOtoProductDAO(productRq));
        return productMapper.mapProductDAOtoProductResponse(savedProductDAO);
    }

    /**
     * Checks if product name is already present in DB.
     *
     * @param productRq product to be checked if exists in DB.
     * @return true if it exists, otherwise, false.
     */
    private boolean productAlreadyExist(ProductApi productRq) {
        return productRepository.findAll()
                .stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(productRq.getName()));
    }

    /**
     * Remove product from DB.
     *
     * @param productId product id to be removed.
     * @throws PaymentException if product does not exist.
     */
    @Override
    public void removeProductById(String productId) throws PaymentException {
        Optional<ProductDAO> product = productRepository.findById(new Long(productId));
        if (product.isPresent()) {
            productRepository.delete(product.get());
        } else {
            throw new PaymentException("Product " + productId + " does not exist");
        }
    }

    /**
     * Update product in DB.
     *
     * @param productRequest product to be updated.
     * @return product updated.
     * @throws PaymentException if product does not exist in DB.
     */
    @Override
    public ProductResponse updateProduct(ProductApi productRequest) throws PaymentException {
        if (productRepository.existsById(productRequest.getId())) {
            productRepository.updateProduct(productRequest.getId(), productRequest.getName(),
                    productRequest.getPrice(), productRequest.getStock());
            return productMapper.mapProductDAOtoProductResponse(
                    productRepository.findById(productRequest.getId()).get());
        } else {
            throw new PaymentException("Product " + productRequest.getId() + " was not found!");
        }
    }

    /**
     * Retrieve all products from DB.
     *
     * @return list of products.
     */
    @Override
    public List<ProductResponse> getAllProducts() {
        return productMapper.mapProductDAOListToProductResponseList(
                productRepository.findAll());
    }

    /**
     * Retrieve product from DB.
     *
     * @param productId product to be retrieved.
     * @return product from DB.
     */
    @Override
    public ProductResponse findByProductId(String productId) {
        Optional<ProductDAO> productDAO = productRepository.findById(new Long(productId));
        return productDAO.map(dao -> productMapper.mapProductDAOtoProductResponse(dao)).orElse(null);
    }
}
