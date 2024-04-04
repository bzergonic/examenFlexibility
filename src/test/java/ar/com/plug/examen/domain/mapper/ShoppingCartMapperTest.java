package ar.com.plug.examen.domain.mapper;

import ar.com.plug.examen.app.api.ProductApi;
import ar.com.plug.examen.app.api.ShoppingCartApi;
import ar.com.plug.examen.domain.dao.ProductDAO;
import ar.com.plug.examen.domain.dao.ShoppingCartDAO;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ShoppingCartResponse;
import ar.com.plug.examen.domain.util.CartStatusEnum;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartMapperTest {

    private ShoppingCartMapper shoppingCartMapper = new ShoppingCartMapper();

    @Test
    public void mapShoppingCartApiToShoppingCartDaoShouldReturnShoppingCartDao() throws PaymentException {
        List<ProductApi> items = new ArrayList<>();
        ProductApi product = new ProductApi(1, "test", 232, 33);
        product.setQuantity(2);
        items.add(product);
        ShoppingCartApi shoppingCartApi = new ShoppingCartApi(123, 32132, items);
        List<ProductDAO> products = new ArrayList<>();
        ProductDAO productDAO = new ProductDAO();
        productDAO.setId(1);
        productDAO.setPrice(232);
        products.add(productDAO);

        ShoppingCartDAO actualResult = shoppingCartMapper.mapShoppingCartApiToShoppingCartDao(shoppingCartApi, products);

        Assertions.assertEquals(shoppingCartApi.getDni(), actualResult.getDni());
        Assertions.assertEquals(shoppingCartApi.getOrderId(), actualResult.getOrderId());
        Assertions.assertEquals(product.getPrice() * product.getQuantity(),
                actualResult.getTotalAmount());
    }

    @Test(expected = PaymentException.class)
    public void mapShoppingCartApiToShoppingCartDaoShouldThrowPaymentExceptionForProductNotFound() throws PaymentException {
        List<ProductApi> items = new ArrayList<>();
        ProductApi product = new ProductApi(1, "test", 232, 33);
        product.setQuantity(2);
        items.add(product);
        ShoppingCartApi shoppingCartApi = new ShoppingCartApi(123, 32132, items);

        ShoppingCartDAO actualResult = shoppingCartMapper.mapShoppingCartApiToShoppingCartDao(shoppingCartApi, new ArrayList<>());
    }

    @Test
    public void mapShoppingCartDaoListToShoppingCartResponseListShouldReturnShopCartResponseList() {
        List<ShoppingCartDAO> shopCartDaoList = new ArrayList<>();
        shopCartDaoList.add(new ShoppingCartDAO(123, 12345, 23333, CartStatusEnum.PAID.toString()));
        List<ShoppingCartResponse> shoppingCartResponses =
                shoppingCartMapper.mapShoppingCartDaoListToShoppingCartResponseList(shopCartDaoList);
        Assertions.assertEquals(shopCartDaoList.get(0).getDni(), shoppingCartResponses.get(0).getDni());
        Assertions.assertEquals(shopCartDaoList.get(0).getOrderId(), shoppingCartResponses.get(0).getOrderId());
    }
}