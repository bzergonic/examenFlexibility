package ar.com.plug.examen.domain.mapper;

import ar.com.plug.examen.app.api.ClientApi;
import ar.com.plug.examen.domain.dao.ClientDAO;
import ar.com.plug.examen.domain.model.ClientResponse;
import org.springframework.context.annotation.Configuration;

/**
 * Mapper class for Client.
 */
@Configuration
public class ClientMapper {

    /**
     * Maps ClientAPI request to ClientDAO.
     *
     * @param clientRequest request to be mapped.
     * @return ClientDao mapped from request.
     */
    public ClientDAO mapClientDTOtoClientDAO(ClientApi clientRequest) {
        ClientDAO clientDAO = new ClientDAO();
        clientDAO.setDni(clientRequest.getDni());
        clientDAO.setName(clientRequest.getName());
        clientDAO.setSurname(clientRequest.getSurname());
        return clientDAO;
    }

    /**
     * Maps ClientDao to ClientResponse.
     *
     * @param clientDao clientDao to be mapped.
     * @return clientResponse mapped from dao.
     */
    public ClientResponse mapClientDAOtoClientResponse(ClientDAO clientDao) {
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setDni(clientDao.getDni());
        clientResponse.setName(clientDao.getName());
        clientResponse.setSurname(clientDao.getSurname());
        return clientResponse;
    }
}
