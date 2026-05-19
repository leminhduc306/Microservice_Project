package vn.test.order_service.dto.response;

import lombok.Getter;
import lombok.Setter;
import vn.test.order_service.enums.OrderStatus;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class OrderRes {
    private String id;
    private String customerId;
    private OrderStatus status;
    private Integer totalAmount;
    private List<OrderItemRes> items;
    private Instant createdDate;
}
