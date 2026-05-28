package vn.test.product_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LockProductReq {
    @Valid
    @NotEmpty(message = "items cannot be empty")
    private List<LockProductItem> items;
}
