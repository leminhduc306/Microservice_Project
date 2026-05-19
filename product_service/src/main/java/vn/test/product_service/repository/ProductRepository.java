package vn.test.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.test.product_service.entity.Product;

public interface ProductRepository  extends JpaRepository<Product, String> {
}
