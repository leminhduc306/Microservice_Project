package vn.test.product_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.test.product_service.consumer.dto.Order;
import vn.test.product_service.consumer.dto.OrderCreatedEvent;
import vn.test.product_service.dto.request.LockProductItem;
import vn.test.product_service.dto.request.LockProductReq;
import vn.test.product_service.service.ProductService;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order_created", groupId = "product-service")
    public void handleOrderCreatedEvent(String orderString) throws JsonProcessingException {

        OrderCreatedEvent orderCreatedEvent =
                objectMapper.readValue(orderString, OrderCreatedEvent.class);

        log.info("Receive order message: {}", orderCreatedEvent);

        List<LockProductItem> lockProductItems = new ArrayList<>();

        orderCreatedEvent.getOrderItems().forEach(orderItem -> {
            LockProductItem lockProductItem = new LockProductItem();

            lockProductItem.setId(orderItem.getProductId());
            lockProductItem.setQuantity(orderItem.getQuantity());

            lockProductItems.add(lockProductItem);
        });

        LockProductReq lockProductReq = new LockProductReq();
        lockProductReq.setItems(lockProductItems);

        productService.lock(lockProductReq);

        log.info("Success to lock product item of order: {}", orderCreatedEvent.getId());
    }
}
