package vn.test.order_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.test.order_service.clients.ProductClient;
import vn.test.order_service.dto.ProductDTO;
import vn.test.order_service.dto.request.DeductStockReq;
import vn.test.order_service.dto.request.OrderItemReq;
import vn.test.order_service.dto.request.OrderReq;
import vn.test.order_service.dto.request.ProductFilter;
import vn.test.order_service.dto.response.OrderRes;
import vn.test.order_service.entity.Order;
import vn.test.order_service.entity.OrderItem;
import vn.test.order_service.enums.OrderStatus;
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

    @Override
    @Transactional
    public OrderRes create(OrderReq orderReq) {
        List<String> productIds = orderReq.getItems().stream()
                .map(OrderItemReq::getProductId)
                .distinct()
                .toList();

        List<ProductDTO> products = productClient.getProductsByIds(new ProductFilter(productIds));
        Map<String, ProductDTO> productPriceMap = new HashMap<>();

        products.forEach(product -> {
            productPriceMap.put(product.getId(), product);
        });

        Order order = Order.builder().
                customerId(orderReq.getCustomerId()).
                status(OrderStatus.PENDING).
                totalAmount(0).
                build();

        Order savedOrder = orderRepository.save(order);

        int totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        Map<String, Integer> deductQtyMap = new HashMap<>();

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

            deductQtyMap.merge(itemDTO.getProductId(), itemDTO.getQuantity(), Integer::sum);
            totalAmount += price * itemDTO.getQuantity();
        }

        List<DeductStockReq> deductStockReqs = deductQtyMap.entrySet().stream()
                .map(entry -> new DeductStockReq(entry.getKey(), entry.getValue()))
                .toList();
        productClient.deductStock(deductStockReqs);

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);

        savedOrder.setTotalAmount(totalAmount);
        savedOrder.setItems(savedOrderItems);

        return orderMapper.toResponse(orderRepository.save(savedOrder));
    }


}
