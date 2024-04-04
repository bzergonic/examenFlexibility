package ar.com.plug.examen.domain.mapper;

import ar.com.plug.examen.app.api.ClientApi;
import ar.com.plug.examen.domain.dao.ClientDAO;
import ar.com.plug.examen.domain.model.ClientResponse;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ClientMapperTest {

    private ClientMapper clientMapper = new ClientMapper();

    @Test
    public void mapClientDTOtoClientDAOShouldReturnMappedClientDao() {
        ClientApi clientDto = new ClientApi();
        clientDto.setDni(12345);
        clientDto.setName("Test");
        ClientDAO result = clientMapper.mapClientDTOtoClientDAO(clientDto);
        Assertions.assertEquals(clientDto.getDni(), result.getDni());
        Assertions.assertEquals(clientDto.getName(), result.getName());
    }

    @Test
    public void mapClientDAOtoClientResponseShouldReturnMappedClientDTO() {
        ClientDAO clientDao = new ClientDAO();
        clientDao.setDni(12345);
        clientDao.setName("Test");
        ClientResponse clientResponse = clientMapper.mapClientDAOtoClientResponse(clientDao);
        Assertions.assertEquals(clientDao.getDni(), clientResponse.getDni());
        Assertions.assertEquals(clientDao.getName(), clientResponse.getName());

    }
}