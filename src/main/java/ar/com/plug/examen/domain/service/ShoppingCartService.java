package ar.com.plug.examen.domain.service;

import ar.com.plug.examen.app.api.ShoppingCartApi;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ShoppingCartResponse;

public interface ShoppingCartService {
    ShoppingCartResponse createOrder(ShoppingCartApi shoppingCartApi) throws PaymentException;

    ShoppingCartResponse approveOrder(long orderId) throws PaymentException;

    ShoppingCartResponse payOrder(long orderId) throws PaymentException;
}