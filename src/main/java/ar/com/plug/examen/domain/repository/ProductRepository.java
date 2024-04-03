package ar.com.plug.examen.domain.repository;

import ar.com.plug.examen.domain.dao.ProductDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@EnableJpaRepositories
public interface ProductRepository extends JpaRepository<ProductDAO, Long> {
    @Transactional
    @Modifying(clearAutomatically=true)
    @Query("update ProductDAO p set p.name = :name, p.price = :price, p.stock = :stock where p.id = :id")
    void updateProduct(@Param(value = "id") Long dni,
                        @Param(value = "name") String name,
                        @Param(value = "price") Double price,
                        @Param(value = "stock") Integer stock);
}
