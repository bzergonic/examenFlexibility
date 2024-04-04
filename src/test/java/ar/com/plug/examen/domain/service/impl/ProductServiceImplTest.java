package ar.com.plug.examen.domain.service.impl;

import ar.com.plug.examen.app.api.ProductApi;
import ar.com.plug.examen.domain.dao.ProductDAO;
import ar.com.plug.examen.domain.mapper.ProductMapper;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ProductResponse;
import ar.com.plug.examen.domain.repository.ProductRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;

    @Test
    public void addProductShouldReturnNewAddedProduct() throws PaymentException {
        ProductApi productDto = new ProductApi(1, "Test", 100, 100);
        ProductDAO productDao = createProductDaoFromDto(productDto);
        ProductResponse expectedResponse = createProductResponseFromDAO(productDao);
        Mockito.when(productRepository.findAll()).thenReturn(new ArrayList<>());
        Mockito.when(productMapper.mapProductDTOtoProductDAO(productDto)).thenReturn(productDao);
        Mockito.when(productRepository.saveAndFlush(productDao)).thenReturn(productDao);
        Mockito.when(productMapper.mapProductDAOtoProductResponse(productDao)).thenReturn(expectedResponse);

        ProductResponse actualResponse = productService.addProduct(productDto);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test(expected = PaymentException.class)
    public void addProductShouldThrowErrorForAlreadyExistingProduct() throws PaymentException {
        ProductApi productDto = new ProductApi(1, "Test", 100, 100);
        ProductDAO productDao = createProductDaoFromDto(productDto);
        List<ProductDAO> productDAOList = new ArrayList<>();
        productDAOList.add(productDao);
        Mockito.when(productRepository.findAll()).thenReturn(productDAOList);

        productService.addProduct(productDto);
    }

    @Test(expected = PaymentException.class)
    public void removeProductByIdShouldThrowExceptionWhenProductDoesNotExist() throws PaymentException {
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        productService.removeProductById("1234");
    }

    @Test
    public void updateProductShouldReturnUpdatedProduct() throws PaymentException {
        ProductApi productDto = new ProductApi(1, "Test", 100, 100);
        Optional<ProductDAO> productOptional = Optional.of(createProductDaoFromDto(productDto));
        ProductResponse expectedResponse = createProductResponseFromDAO(productOptional.get());
        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.doNothing().when(productRepository).updateProduct(Mockito.anyLong(), Mockito.anyString(),
                Mockito.anyDouble(), Mockito.anyInt());
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(productOptional);
        Mockito.when(productMapper.mapProductDAOtoProductResponse(productOptional.get())).thenReturn(expectedResponse);

        ProductResponse actualResponse = productService.updateProduct(productDto);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test(expected = PaymentException.class)
    public void updateProductShouldThrowPaymentException() throws PaymentException {
        ProductApi productDto = new ProductApi(1, "Test", 100, 100);
        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenReturn(false);
        productService.updateProduct(productDto);
    }

    @Test
    public void getAllProductsShouldReturnProductResponseList() {
        ProductDAO productDao = createProductDaoFromDto(new ProductApi(1, "Test", 100, 100));
        List<ProductDAO> products = new ArrayList<>();
        products.add(productDao);
        List<ProductResponse> expectedResponse = new ArrayList<>();
        expectedResponse.add(createProductResponseFromDAO(productDao));
        Mockito.when(productRepository.findAll()).thenReturn(products);
        Mockito.when(productMapper.mapProductDAOListToProductResponseList(products)).thenReturn(expectedResponse);

        List<ProductResponse> actualResponse = productService.getAllProducts();

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void findByProductIdShouldReturnProductResponse() {
        ProductDAO productDao = createProductDaoFromDto(new ProductApi(1, "Test", 100, 100));
        ProductResponse expectedResponse = createProductResponseFromDAO(productDao);
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(productDao));
        Mockito.when(productMapper.mapProductDAOtoProductResponse(productDao)).thenReturn(expectedResponse);

        ProductResponse actualResponse = productService.findByProductId("1");

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    private ProductResponse createProductResponseFromDAO(ProductDAO productDao) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(productDao.getId());
        productResponse.setName(productDao.getName());
        productResponse.setPrice(productDao.getPrice());
        productResponse.setStock(productResponse.getStock());
        return null;
    }

    private ProductDAO createProductDaoFromDto(ProductApi productDto) {
        ProductDAO productDAO = new ProductDAO();
        productDAO.setId(productDto.getId());
        productDAO.setName(productDto.getName());
        productDAO.setPrice(productDto.getPrice());
        productDAO.setStock(productDto.getStock());
        return productDAO;
    }
}