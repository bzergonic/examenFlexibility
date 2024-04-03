package ar.com.plug.examen.domain.mapper;

import ar.com.plug.examen.app.api.ProductApi;
import ar.com.plug.examen.domain.dao.ProductDAO;
import ar.com.plug.examen.domain.model.ProductResponse;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for Product.
 */
@Configuration
public class ProductMapper {

    /**
     * Maps Product request to Product DAO.
     *
     * @param product product to be mapped.
     * @return ProductDao mapped from request.
     */
    public ProductDAO mapProductDTOtoProductDAO(ProductApi product) {
        ProductDAO productDAO = new ProductDAO();
        productDAO.setId(product.getId());
        productDAO.setName(product.getName());
        productDAO.setPrice(product.getPrice());
        productDAO.setStock(product.getStock());
        return productDAO;
    }

    /**
     * Maps list of productDao to list of productResponse.
     *
     * @param productDaoList list of productDao to be mapped.
     * @return list of productResponse.
     */
    public List<ProductResponse> mapProductDAOListToProductResponseList(List<ProductDAO> productDaoList) {
        return productDaoList.stream()
                .map(this::mapProductDAOtoProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps productDao to productResponse.
     *
     * @param productDAO productDao to be mapped.
     * @return productResponse.
     */
    public ProductResponse mapProductDAOtoProductResponse(ProductDAO productDAO) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(productDAO.getId());
        productResponse.setName(productDAO.getName());
        productResponse.setPrice(productDAO.getPrice());
        productResponse.setStock(productDAO.getStock());
        return productResponse;
    }
}