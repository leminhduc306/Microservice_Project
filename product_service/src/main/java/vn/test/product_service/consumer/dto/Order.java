package vn.test.product_service.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private String id;
    private String customerId;
    private String status;
    private Integer totalAmount;
}
