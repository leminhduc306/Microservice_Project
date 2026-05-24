package vn.test.order_service.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.test.order_service.enums.OrderStatus;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@Table(name = "orders")
@AllArgsConstructor
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "customer_id")
    private String customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items;
}
