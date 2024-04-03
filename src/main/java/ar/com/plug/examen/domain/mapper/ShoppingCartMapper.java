package ar.com.plug.examen.domain.mapper;

import ar.com.plug.examen.app.api.ProductApi;
import ar.com.plug.examen.app.api.ShoppingCartApi;
import ar.com.plug.examen.domain.dao.ProductDAO;
import ar.com.plug.examen.domain.dao.ShoppingCartDAO;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ShoppingCartResponse;
import ar.com.plug.examen.domain.util.CartStatusEnum;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper class for Shopping Cart.
 */
@Configuration
public class ShoppingCartMapper {

    /**
     * Maps ShoppingCart to ShoppingCartDao.
     *
     * @param shoppingCartApi object to be mapped.
     * @param allProducts products to check price and calculate totalAmount in productDao.
     * @return shoppingCartDao mapped from request.
     * @throws PaymentException if any product is not existent in DB.
     */
    public ShoppingCartDAO mapShoppingCartApiToShoppingCartDao(ShoppingCartApi shoppingCartApi,
                                                               List<ProductDAO> allProducts) throws PaymentException {
        ShoppingCartDAO shoppingCartDAO = new ShoppingCartDAO();
        shoppingCartDAO.setDni(shoppingCartApi.getDni());
        shoppingCartDAO.setStatus(CartStatusEnum.NOT_PAID.toString());
        shoppingCartDAO.setTotalAmount(calculateTotalAmount(shoppingCartApi.getItems(), allProducts));
        return shoppingCartDAO;
    }

    /**
     * Calculate the total amount of items in the cart.
     *
     * @param items items in order cart.
     * @param allProducts products to check price and calculate totalAmount.
     * @return totalAmount value.
     * @throws PaymentException if any product is not existent in DB.
     */
    private double calculateTotalAmount(List<ProductApi> items, List<ProductDAO> allProducts) throws PaymentException {
        Map<Long, ProductDAO> productsMap = allProducts
                .stream()
                .collect(Collectors.toMap(ProductDAO::getId, Function.identity()));
        if (items
                .stream()
                .anyMatch(productApi -> productsMap.get(productApi.getId()) == null)) {
            throw new PaymentException("One or more products ID are not valid. Please check and retry.");
        }
        return items
                .stream()
                .map(product -> productsMap.get(product.getId()).getPrice() * product.getQuantity())
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Maps ShoppingCartDAO to ShoppingCartResponse.
     *
     * @param shoppingCartDAO object to be mapped.
     * @return ShoppingCartResponse object.
     */
    public ShoppingCartResponse mapShoppingCartDaoToShoppingCartResponse(ShoppingCartDAO shoppingCartDAO) {
        ShoppingCartResponse shoppingCartResponse = new ShoppingCartResponse();
        shoppingCartResponse.setOrderId(shoppingCartDAO.getOrderId());
        shoppingCartResponse.setDni(shoppingCartDAO.getDni());
        shoppingCartResponse.setTotalAmount(shoppingCartDAO.getTotalAmount());
        shoppingCartResponse.setStatus(CartStatusEnum.valueOf(shoppingCartDAO.getStatus()));
        return shoppingCartResponse;
    }

    /**
     * Maps list of ShoppingCartDAO to list of ShoppingCartResponse.
     *
     * @param ordersByDni object list to be mapped.
     * @return list of ShoppingCartResponse object.
     */
    public List<ShoppingCartResponse> mapShoppingCartDaoListToShoppingCartResponseList(List<ShoppingCartDAO> ordersByDni) {
        return ordersByDni
                .stream()
                .map(this::mapShoppingCartDaoToShoppingCartResponse)
                .collect(Collectors.toList());
    }
}
