package vn.test.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.test.product_service.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
}
