package vn.test.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.test.order_service.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
}
