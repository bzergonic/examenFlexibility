package ar.com.plug.examen.domain.service.impl;

import ar.com.plug.examen.app.api.ClientApi;
import ar.com.plug.examen.domain.dao.ClientDAO;
import ar.com.plug.examen.domain.dao.ShoppingCartDAO;
import ar.com.plug.examen.domain.mapper.ClientMapper;
import ar.com.plug.examen.domain.mapper.ShoppingCartMapper;
import ar.com.plug.examen.domain.model.ClientResponse;
import ar.com.plug.examen.domain.model.PaymentException;
import ar.com.plug.examen.domain.model.ShoppingCartResponse;
import ar.com.plug.examen.domain.repository.ClientRepository;
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
public class ClientServiceImplTest {

    @InjectMocks
    private ClientServiceImpl clientService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Test
    public void addClientShouldReturnNewAddedClient() throws PaymentException {
        ClientApi clientRQ = createClientApiObject();
        ClientDAO clientDao = clientDaoFromClientRQ(clientRQ);
        ClientResponse expectedResponse = createClientResponseFromClientDao(clientDao);
        Mockito.when(clientRepository.existsById(Mockito.anyInt())).thenReturn(false);
        Mockito.when(clientMapper.mapClientDTOtoClientDAO(clientRQ)).thenReturn(clientDao);
        Mockito.when(clientRepository.saveAndFlush(clientDao)).thenReturn(clientDao);
        Mockito.when(clientMapper.mapClientDAOtoClientResponse(clientDao)).thenReturn(expectedResponse);

        ClientResponse actualResponse = clientService.addClient(clientRQ);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test(expected = PaymentException.class)
    public void addClientShouldThrowPaymentExceptionForExistingClient() throws PaymentException {
        ClientApi clientRQ = createClientApiObject();
        Mockito.when(clientRepository.existsById(Mockito.anyInt())).thenReturn(true);
        clientService.addClient(clientRQ);
    }

    @Test
    public void addClientShouldThrowPaymentExceptionWithExpectedMessageForExistingClient() {
        ClientApi clientRQ = createClientApiObject();
        String expectedErrorMessage = "Client with DNI " + clientRQ.getDni() + " already exist.";
        Mockito.when(clientRepository.existsById(Mockito.anyInt())).thenReturn(true);
        try {
            clientService.addClient(clientRQ);
        } catch (PaymentException e) {
            Assertions.assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void removeClientByDniShouldThrowPaymentExceptionForNotExistingClient() {
        Optional<ClientDAO> clientOptional = Optional.empty();
        String expectedMessage = "The client with DNI 12345 does not exist";
        Mockito.when(clientRepository.findById(12345)).thenReturn(clientOptional);
        try {
            clientService.removeClientByDni(12345);
        } catch (PaymentException e) {
            Assertions.assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    public void updateClientShouldReturnUpdatedClientResponse() throws PaymentException {
        ClientApi clientRq = createClientApiObject();
        ClientDAO clientDAO = clientDaoFromClientRQ(clientRq);
        Optional<ClientDAO> clientDaoOptional = Optional.of(clientDAO);
        ClientResponse expectedResponse = createClientResponseFromClientDao(clientDAO);
        Mockito.when(clientRepository.existsById(Mockito.anyInt())).thenReturn(true);
        Mockito.doNothing().when(clientRepository).updateClient(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(clientRepository.findById(Mockito.anyInt())).thenReturn(clientDaoOptional);
        Mockito.when(clientMapper.mapClientDAOtoClientResponse(clientDAO)).thenReturn(expectedResponse);

        ClientResponse actualResponse = clientService.updateClient(clientRq);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void updateClientShouldThrowPaymentExceptionForNonExistingClient() {
        ClientApi clientRq = createClientApiObject();
        String expected = "Client with DNI " + clientRq.getDni() + " was not found.";
        Mockito.when(clientRepository.existsById(Mockito.anyInt())).thenReturn(false);

        try {
            clientService.updateClient(clientRq);
        } catch (PaymentException e) {
            Assertions.assertEquals(expected, e.getMessage());
        }
    }

    @Test
    public void getOrdersByDniShouldReturnOrderList() throws PaymentException {
        List<ShoppingCartDAO> orders = createShoppingCartDaoList();
        List<ShoppingCartResponse> expectedList = createShoppingCartResponseListFromDaoList(orders);
        Mockito.when(clientRepository.existsById(Mockito.anyInt())).thenReturn(true);
        Mockito.when(shoppingCartRepository.getOrdersByDni(12345)).thenReturn(orders);
        Mockito.when(shoppingCartMapper.mapShoppingCartDaoListToShoppingCartResponseList(orders))
                .thenReturn(expectedList);

        List<ShoppingCartResponse> actualList = clientService.getOrdersByDni(12345);

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    public void getOrdersByDniShouldThrowPaymentExceptionForNonExistingDni() {
        String expectedReuslt = "Client with DNI 12345 does not exist. Please add it and retry.";
        Mockito.when(clientRepository.existsById(Mockito.anyInt())).thenReturn(false);
        try {
            clientService.getOrdersByDni(12345);
        } catch (PaymentException e) {
            Assertions.assertEquals(expectedReuslt, e.getMessage());
        }
    }

    private List<ShoppingCartResponse> createShoppingCartResponseListFromDaoList(List<ShoppingCartDAO> orders) {
        return orders.stream().map(o -> {
            ShoppingCartResponse shoppingCartResponse = new ShoppingCartResponse();
            shoppingCartResponse.setDni(o.getDni());
            shoppingCartResponse.setOrderId(o.getOrderId());
            shoppingCartResponse.setTotalAmount(o.getTotalAmount());
            shoppingCartResponse.setStatus(CartStatusEnum.valueOf(o.getStatus()));
            return shoppingCartResponse;
        }).collect(Collectors.toList());
    }

    private List<ShoppingCartDAO> createShoppingCartDaoList() {
        List<ShoppingCartDAO> shoppingCartDAOList = new ArrayList<>();
        shoppingCartDAOList.add(new ShoppingCartDAO(1, 12345, 500, CartStatusEnum.NOT_PAID.toString()));
        shoppingCartDAOList.add(new ShoppingCartDAO(2, 12345, 254, CartStatusEnum.APPROVED.toString()));
        return shoppingCartDAOList;
    }

    private ClientResponse createClientResponseFromClientDao(ClientDAO clientDao) {
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setDni(clientDao.getDni());
        clientResponse.setName(clientDao.getName());
        clientResponse.setSurname(clientResponse.getSurname());
        return clientResponse;
    }

    private ClientDAO clientDaoFromClientRQ(ClientApi clientRQ) {
        ClientDAO clientDAO = new ClientDAO();
        clientDAO.setDni(clientRQ.getDni());
        clientDAO.setName(clientRQ.getName());
        clientDAO.setSurname(clientRQ.getSurname());
        return clientDAO;
    }

    private ClientApi createClientApiObject() {
        ClientApi clientApi = new ClientApi();
        clientApi.setDni(12345);
        clientApi.setName("TestName");
        clientApi.setSurname("Surname");
        return clientApi;
    }
}