package vn.test.product_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LockProductItem {
    @NotBlank(message = "id cannot be blank")
    private String id;

    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;
}
