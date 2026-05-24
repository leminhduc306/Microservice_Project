package vn.test.order_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private String id;

    private String name;

    private Integer price;

    private Integer stock;
}
