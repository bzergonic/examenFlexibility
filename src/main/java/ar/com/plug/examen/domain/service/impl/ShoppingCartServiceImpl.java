package ar.com.plug.examen.domain.service.impl;

import ar.com.plug.examen.app.api.ShoppingCartApi;
import ar.com.plug.examen.domain.dao.ProductDAO;
import ar.com.plug.examen.domain.dao.ShoppingCartDAO;
import ar.com.plug.examen.domain.mapper.ShoppingCartMapper;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ShoppingCartResponse;
import ar.com.plug.examen.domain.repository.ClientRepository;
import ar.com.plug.examen.domain.repository.ProductRepository;
import ar.com.plug.examen.domain.repository.ShoppingCartRepository;
import ar.com.plug.examen.domain.service.ShoppingCartService;
import ar.com.plug.examen.domain.util.CartStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Shopping Cart service operations.
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * Create order in DB.
     *
     * @param shoppingCartApi order to be saved in DB.
     * @return order created with total amount.
     * @throws PaymentException if client does not exist.
     */
    @Override
    public ShoppingCartResponse createOrder(ShoppingCartApi shoppingCartApi) throws PaymentException {
        if (clientRepository.existsById(shoppingCartApi.getDni())) {
            ShoppingCartDAO shoppingCartDAO = shoppingCartRepository.saveAndFlush(
                    shoppingCartMapper.mapShoppingCartApiToShoppingCartDao(shoppingCartApi, getAllProducts()));
            return shoppingCartMapper.mapShoppingCartDaoToShoppingCartResponse(shoppingCartDAO);
        } else {
            throw new PaymentException("Client with DNI " + shoppingCartApi.getDni() + " does not exist. Please add it and retry.");
        }
    }

    /**
     * Changes the status in DB for given order.
     *
     * @param orderId order to be updated.
     * @return updated order.
     * @throws PaymentException if order does not exist in DB.
     */
    @Override
    public ShoppingCartResponse approveOrder(long orderId) throws PaymentException {
        return updateStatus(orderId, CartStatusEnum.NOT_PAID, CartStatusEnum.APPROVED);
    }

    /**
     * Changes the status in DB for given order.
     *
     * @param orderId order to be updated.
     * @return updated order.
     * @throws PaymentException if order does not exist in DB.
     */
    @Override
    public ShoppingCartResponse payOrder(long orderId) throws PaymentException {
        return updateStatus(orderId, CartStatusEnum.APPROVED, CartStatusEnum.PAID);
    }

    /**
     * Update status as approved or paid.
     * Also checks if already approved, it cannot approve it again. If so, throws exception.
     * The same as above for paid status.
     *
     * @param orderId order to be updated.
     * @param previousStatus previous status to be checked if correct.
     * @param newStatus new status after update.
     * @return updated order.
     * @throws PaymentException if order does not exist, or if status is not correct.
     */
    private ShoppingCartResponse updateStatus(long orderId, CartStatusEnum previousStatus, CartStatusEnum newStatus)
            throws PaymentException {
        Optional<ShoppingCartDAO> orderOptional = shoppingCartRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            if (previousStatus.toString().equalsIgnoreCase(orderOptional.get().getStatus())) {
                shoppingCartRepository.updateOrderStatus(orderId, newStatus.toString());
                return shoppingCartMapper.mapShoppingCartDaoToShoppingCartResponse(
                        shoppingCartRepository.findById(orderId).get());
            }
            throw new PaymentException("Order must be in " + previousStatus +
                    " status before " + (CartStatusEnum.PAID.equals(newStatus) ? "paying" : "approving") + ".");
        }
        throw new PaymentException("Order Id " + orderId + " does not exist.");
    }

    /**
     * Retrieves all products from Product table from DB.
     *
     * @return all products from DB.
     */
    private List<ProductDAO> getAllProducts() {
        return productRepository.findAll();
    }
}
