package vn.test.order_service.events;


import lombok.*;
import vn.test.order_service.entity.Order;
import vn.test.order_service.entity.OrderItem;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderCreatedEvent extends Order {
    private List<OrderItem> orderItems;
}
