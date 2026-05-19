package vn.test.order_service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRes {
    private String id;
    private String productId;
    private Integer price;
    private Integer quantity;
}
