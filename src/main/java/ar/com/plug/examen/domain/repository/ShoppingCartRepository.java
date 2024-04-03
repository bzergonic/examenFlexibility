package ar.com.plug.examen.domain.repository;

import ar.com.plug.examen.domain.dao.ShoppingCartDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface ShoppingCartRepository extends JpaRepository<ShoppingCartDAO, Long> {

    @Query("select new ShoppingCartDAO(sc.orderId, sc.dni, sc.totalAmount, sc.status) from ShoppingCartDAO sc where sc.dni = :dni")
    List<ShoppingCartDAO> getOrdersByDni(@Param(value = "dni") int dni);

    @Transactional
    @Modifying(clearAutomatically=true)
    @Query("update ShoppingCartDAO sc set sc.status = :status where sc.orderId = :orderId")
    void updateOrderStatus(@Param(value = "orderId") Long orderId, @Param(value = "status") String status);
}
