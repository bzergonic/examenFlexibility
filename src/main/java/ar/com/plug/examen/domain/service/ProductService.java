package ar.com.plug.examen.domain.service;

import ar.com.plug.examen.app.api.ProductApi;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse addProduct(ProductApi productRq) throws PaymentException;

    void removeProductById(String productId) throws PaymentException;

    ProductResponse updateProduct(ProductApi productId) throws PaymentException;

    List<ProductResponse> getAllProducts();

    ProductResponse findByProductId(String productId);
}
