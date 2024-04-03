package ar.com.plug.examen.domain.service;

import ar.com.plug.examen.app.api.ClientApi;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ClientResponse;
import ar.com.plug.examen.domain.model.ShoppingCartResponse;

import java.util.List;

public interface ClientService {

    ClientResponse addClient(ClientApi clientRequest) throws PaymentException;

    void removeClientByDni(int clientDni) throws PaymentException;

    ClientResponse updateClient(ClientApi clientRequest) throws PaymentException;

    List<ShoppingCartResponse> getOrdersByDni(int clientDni) throws PaymentException;
}
