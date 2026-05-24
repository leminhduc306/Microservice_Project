package vn.test.order_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import vn.test.order_service.enums.OrderStatus;

import java.util.List;

@Getter
@Setter
public class OrderReq {
    @NotBlank(message = "customerId cannot be blank")
    private String customerId;

    @NotEmpty(message = "order items cannot be empty")
    @Valid
    private List<OrderItemReq> items;
}