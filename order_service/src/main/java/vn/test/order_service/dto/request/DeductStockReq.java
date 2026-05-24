package vn.test.order_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeductStockReq {
    private String productId;
    private Integer quantity;
}
