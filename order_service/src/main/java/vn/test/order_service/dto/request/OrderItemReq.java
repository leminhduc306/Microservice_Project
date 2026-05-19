package vn.test.order_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemReq {
    @NotBlank(message = "productId cannot be blank")
    private String productId;

    @Min(value = 0, message = "price must be greater than or equal to 0")
    private Integer price;

    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;
}
