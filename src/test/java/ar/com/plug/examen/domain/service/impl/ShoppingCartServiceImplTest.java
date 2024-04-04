package ar.com.plug.examen.domain.service.impl;

import ar.com.plug.examen.app.api.ProductApi;
import ar.com.plug.examen.app.api.ShoppingCartApi;
import ar.com.plug.examen.domain.dao.ProductDAO;
import ar.com.plug.examen.domain.dao.ShoppingCartDAO;
import ar.com.plug.examen.domain.mapper.ShoppingCartMapper;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ShoppingCartResponse;
import ar.com.plug.examen.domain.repository.ClientRepository;
import ar.com.plug.examen.domain.repository.ProductRepository;
import ar.com.plug.examen.domain.repository.ShoppingCartRepository;
import ar.com.plug.examen.domain.util.CartStatusEnum;
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
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartServiceImplTest {

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Test
    public void createOrderShouldReturnValidShoppingCartResponse() throws PaymentException {
        List<ProductApi> products = new ArrayList<>();
        products.add(new ProductApi(123, "test", 200, 125));
        ShoppingCartApi shoppingCartRq = new ShoppingCartApi(1, 12345, products);
        ShoppingCartDAO shoppingCartDao = createShoppingCartDaoFromDto(shoppingCartRq);
        ShoppingCartResponse expectedResponse = createShoppingCartResponseFromDao(shoppingCartDao);
        Mockito.when(clientRepository.existsById(Mockito.anyInt())).thenReturn(true);
        Mockito.when(productRepository.findAll()).thenReturn(productsDtoToProductsDAO(products));
        Mockito.when(shoppingCartMapper.mapShoppingCartApiToShoppingCartDao(Mockito.any(), Mockito.anyList()))
                .thenReturn(shoppingCartDao);
        Mockito.when(shoppingCartRepository.saveAndFlush(shoppingCartDao)).thenReturn(shoppingCartDao);
        Mockito.when(shoppingCartMapper.mapShoppingCartDaoToShoppingCartResponse(shoppingCartDao)).thenReturn(expectedResponse);

        ShoppingCartResponse actualResponse = shoppingCartService.createOrder(shoppingCartRq);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test(expected = PaymentException.class)
    public void createOrderShouldThrowPaymentExceptionForNonExistingClient() throws PaymentException {
        List<ProductApi> products = new ArrayList<>();
        products.add(new ProductApi(123, "test", 200, 125));
        ShoppingCartApi shoppingCartRq = new ShoppingCartApi(1, 12345, products);
        Mockito.when(clientRepository.existsById(Mockito.anyInt())).thenReturn(false);
        shoppingCartService.createOrder(shoppingCartRq);
    }

    @Test
    public void approveOrderShouldReturnValidShoppingCartResponseWithApprovedStatus() throws PaymentException {
        List<ProductApi> products = new ArrayList<>();
        products.add(new ProductApi(123, "test", 200, 125));
        ShoppingCartApi shoppingCartRq = new ShoppingCartApi(1, 12345, products);
        ShoppingCartDAO shoppingCartDao = createShoppingCartDaoFromDto(shoppingCartRq);
        ShoppingCartResponse expectedResponse = createShoppingCartResponseFromDao(shoppingCartDao);
        expectedResponse.setStatus(CartStatusEnum.APPROVED);
        Mockito.when(shoppingCartRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(shoppingCartDao));
        Mockito.doNothing().when(shoppingCartRepository).updateOrderStatus(Mockito.anyLong(), Mockito.anyString());
        Mockito.when(shoppingCartRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(shoppingCartDao));
        Mockito.when(shoppingCartMapper.mapShoppingCartDaoToShoppingCartResponse(shoppingCartDao)).thenReturn(expectedResponse);

        ShoppingCartResponse actualResponse = shoppingCartService.approveOrder(1234);

        Assertions.assertEquals(expectedResponse, actualResponse);
        Assertions.assertTrue(CartStatusEnum.APPROVED.equals(actualResponse.getStatus()));
    }

    @Test(expected = PaymentException.class)
    public void approveOrderShouldThrowPaymentExceptionWhenStatusIsApproved() throws PaymentException {
        List<ProductApi> products = new ArrayList<>();
        products.add(new ProductApi(123, "test", 200, 125));
        ShoppingCartApi shoppingCartRq = new ShoppingCartApi(1, 12345, products);
        ShoppingCartDAO shoppingCartDao = createShoppingCartDaoFromDto(shoppingCartRq);
        shoppingCartDao.setStatus("PAID");
        Mockito.when(shoppingCartRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(shoppingCartDao));

        shoppingCartService.approveOrder(1234);
    }

    @Test
    public void payOrderShouldReturnValidShoppingCartResponseWithPaidStatus() throws PaymentException {
        List<ProductApi> products = new ArrayList<>();
        products.add(new ProductApi(123, "test", 200, 125));
        ShoppingCartApi shoppingCartRq = new ShoppingCartApi(1, 12345, products);
        ShoppingCartDAO shoppingCartDao = createShoppingCartDaoFromDto(shoppingCartRq);
        shoppingCartDao.setStatus("APPROVED");
        ShoppingCartResponse expectedResponse = createShoppingCartResponseFromDao(shoppingCartDao);
        expectedResponse.setStatus(CartStatusEnum.PAID);
        Mockito.when(shoppingCartRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(shoppingCartDao));
        Mockito.doNothing().when(shoppingCartRepository).updateOrderStatus(Mockito.anyLong(), Mockito.anyString());
        Mockito.when(shoppingCartRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(shoppingCartDao));
        Mockito.when(shoppingCartMapper.mapShoppingCartDaoToShoppingCartResponse(shoppingCartDao)).thenReturn(expectedResponse);

        ShoppingCartResponse actualResponse = shoppingCartService.payOrder(1234);

        Assertions.assertEquals(expectedResponse, actualResponse);
        Assertions.assertTrue(CartStatusEnum.PAID.equals(actualResponse.getStatus()));
    }

    @Test(expected = PaymentException.class)
    public void payOrderShouldThrowPaymentExceptionWhenOrderDoesNotExist() throws PaymentException {
        Mockito.when(shoppingCartRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        shoppingCartService.payOrder(1234);
    }

    private ShoppingCartResponse createShoppingCartResponseFromDao(ShoppingCartDAO shoppingCartDao) {
        ShoppingCartResponse shoppingCartResponse = new ShoppingCartResponse();
        shoppingCartResponse.setOrderId(shoppingCartDao.getOrderId());
        shoppingCartResponse.setDni(shoppingCartDao.getDni());
        shoppingCartResponse.setTotalAmount(shoppingCartDao.getTotalAmount());
        shoppingCartResponse.setStatus(CartStatusEnum.valueOf(shoppingCartDao.getStatus()));
        return shoppingCartResponse;
    }

    private ShoppingCartDAO createShoppingCartDaoFromDto(ShoppingCartApi shoppingCartRq) {
        ShoppingCartDAO shoppingCartDAO = new ShoppingCartDAO();
        shoppingCartDAO.setOrderId(shoppingCartRq.getOrderId());
        shoppingCartDAO.setStatus(CartStatusEnum.NOT_PAID.toString());
        shoppingCartDAO.setTotalAmount(200);
        shoppingCartDAO.setDni(shoppingCartRq.getDni());
        return shoppingCartDAO;
    }

    private List<ProductDAO> productsDtoToProductsDAO(List<ProductApi> products) {
        return products.stream().map(p -> {
            ProductDAO productDAO = new ProductDAO();
            productDAO.setId(p.getId());
            productDAO.setName(p.getName());
            productDAO.setPrice(p.getPrice());
            productDAO.setStock(p.getStock());
            return productDAO;
        }).collect(Collectors.toList());
    }
}