package vn.test.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.test.product_service.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository  extends JpaRepository<Product, String> {

    List<Product> findByIdIn(List<String> ids);
}
