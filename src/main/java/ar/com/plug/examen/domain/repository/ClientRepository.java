package ar.com.plug.examen.domain.repository;

import ar.com.plug.examen.domain.dao.ClientDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@EnableJpaRepositories
public interface ClientRepository extends JpaRepository<ClientDAO, Integer> {

    @Transactional
    @Modifying(clearAutomatically=true)
    @Query("update ClientDAO c set c.name = :name, c.surname = :surname  where c.dni = :dni")
    void updateClient(@Param(value = "dni") Integer dni,
                                @Param(value = "name") String name,
                                @Param(value = "surname") String surname);
}
