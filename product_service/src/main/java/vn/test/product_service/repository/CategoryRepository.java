package vn.test.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.test.product_service.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
