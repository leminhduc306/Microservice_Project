package vn.test.order_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.test.order_service.clients.ProductClient;
import vn.test.order_service.dto.ProductDTO;
import vn.test.order_service.dto.request.OrderItemReq;
import vn.test.order_service.dto.request.OrderReq;
import vn.test.order_service.dto.request.ProductFilter;
import vn.test.order_service.dto.response.OrderRes;
import vn.test.order_service.entity.Order;
import vn.test.order_service.entity.OrderItem;
import vn.test.order_service.enums.OrderStatus;
import vn.test.order_service.events.OrderCreatedEvent;
import vn.test.order_service.exception.ApplicationException;
import vn.test.order_service.mapper.OrderMapper;
import vn.test.order_service.repository.OrderItemRepository;
import vn.test.order_service.repository.OrderRepository;
import vn.test.order_service.service.OrderService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;
    private final OrderItemRepository orderItemRepository;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    @Override
    @Transactional
    public OrderRes create(OrderReq orderReq) {
        List<String> productIds = orderReq.getItems().stream()
                .map(OrderItemReq::getProductId)
                .distinct()
                .toList();

        List<ProductDTO> products = productClient.getProductsByIds(new ProductFilter(productIds));
        Map<String, ProductDTO> productPriceMap = new HashMap<>();

        products.forEach(product -> productPriceMap.put(product.getId(), product));

        Order order = Order.builder().
                customerId(orderReq.getCustomerId()).
                status(OrderStatus.PENDING).
                totalAmount(0).
                build();

        Order savedOrder = orderRepository.save(order);

        int totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemReq itemDTO : orderReq.getItems()) {

            ProductDTO productDTO = productPriceMap.get(itemDTO.getProductId());

            if (productDTO == null) {
                throw new ApplicationException(
                        "Product " + itemDTO.getProductId() + " not existed"
                );
            }

            if (itemDTO.getQuantity() > productDTO.getStock()) {
                throw new ApplicationException(
                        "Product " + itemDTO.getProductId() + " not enough"
                );
            }

            Integer price = productDTO.getPrice();

            OrderItem item = OrderItem.builder()
                    .orderId(savedOrder.getId())
                    .productId(itemDTO.getProductId())
                    .price(price)
                    .quantity(itemDTO.getQuantity())
                    .build();
            orderItems.add(item);
            totalAmount += price * itemDTO.getQuantity();
        }

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);
        savedOrder.setTotalAmount(totalAmount);
        Order createOrder = orderRepository.save(savedOrder);

        OrderCreatedEvent orderCreatedEvent = orderMapper.toEvent(createOrder);
        orderCreatedEvent.setOrderItems(savedOrderItems);

        kafkaTemplate.send("order_created", orderCreatedEvent);
        log.info("Order created: {}", createOrder);
        return orderMapper.toResponse(createOrder);
    }

    @Override
    @Transactional
    public void changeStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException("Order not found: " + orderId));

        order.setStatus(status);
        orderRepository.save(order);
        log.info("Order status changed: {}, status: {}", orderId, status);
    }
}
