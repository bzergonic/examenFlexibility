package ar.com.plug.examen.domain.service.impl;

import ar.com.plug.examen.app.api.ClientApi;
import ar.com.plug.examen.domain.dao.ClientDAO;
import ar.com.plug.examen.domain.mapper.ClientMapper;
import ar.com.plug.examen.domain.mapper.ShoppingCartMapper;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ClientResponse;
import ar.com.plug.examen.domain.model.ShoppingCartResponse;
import ar.com.plug.examen.domain.repository.ClientRepository;
import ar.com.plug.examen.domain.repository.ShoppingCartRepository;
import ar.com.plug.examen.domain.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Client service operations.
 */
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * Save client into DB.
     *
     * @param clientRequest client to be saved.
     * @return client saved.
     * @throws PaymentException if client already exist.
     */
    @Override
    public ClientResponse addClient(ClientApi clientRequest) throws PaymentException {
        if (clientRepository.existsById(clientRequest.getDni())) {
            throw new PaymentException("Client with DNI " + clientRequest.getDni() + " already exist.");
        }
        ClientDAO clientDao = clientRepository.saveAndFlush(clientMapper.mapClientDTOtoClientDAO(clientRequest));
        return clientMapper.mapClientDAOtoClientResponse(clientDao);
    }

    /**
     * Remove client from DB.
     *
     * @param clientDni client to be removed.
     * @throws PaymentException if client does not exist.
     */
    @Override
    public void removeClientByDni(int clientDni) throws PaymentException {
        Optional<ClientDAO> clientOptional = clientRepository.findById(clientDni);
        if (clientOptional.isPresent()) {
            clientRepository.delete(clientOptional.get());
        } else {
            throw new PaymentException("The client with DNI " + clientDni + " does not exist");
        }
    }

    /**
     * Update client in DB.
     *
     * @param clientRequest client to be updated.
     * @return client updated.
     * @throws PaymentException if client does not exist in DB.
     */
    @Override
    public ClientResponse updateClient(ClientApi clientRequest) throws PaymentException {
        if (clientRepository.existsById(clientRequest.getDni())) {
            clientRepository.updateClient(clientRequest.getDni(), clientRequest.getName(), clientRequest.getSurname());
            return clientMapper.mapClientDAOtoClientResponse(clientRepository.findById(clientRequest.getDni()).get());
        } else {
            throw new PaymentException("Client with DNI " + clientRequest.getDni() + " was not found.");
        }
    }

    /**
     * Retrieves orders for given DNI from DB.
     *
     * @param clientDni client dni for which orders are going to be retrieved.
     * @return orders associated to given dni.
     * @throws PaymentException if client does not exist in DB.
     */
    @Override
    public List<ShoppingCartResponse> getOrdersByDni(int clientDni) throws PaymentException {
        if (!clientRepository.existsById(clientDni)) {
            throw new PaymentException("Client with DNI " + clientDni + " does not exist. Please add it and retry.");
        }
        return shoppingCartMapper.mapShoppingCartDaoListToShoppingCartResponseList(
                shoppingCartRepository.getOrdersByDni(clientDni));
    }
}
