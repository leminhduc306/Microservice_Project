package vn.test.product_service.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.test.product_service.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository  extends JpaRepository<Product, String> {

    List<Product> findByIdIn(List<String> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    List<Product> findByIdInForUpdate(@Param("ids") List<String> ids);}
