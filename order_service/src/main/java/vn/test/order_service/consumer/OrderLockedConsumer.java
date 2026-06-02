package vn.test.order_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.test.order_service.enums.OrderStatus;
import vn.test.order_service.events.OrderLockedEvent;
import vn.test.order_service.service.OrderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderLockedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = "order_locked", groupId = "order-service")
    public void handleOrderLockedEvent(String orderString) throws JsonProcessingException {
        OrderLockedEvent orderLockedEvent = objectMapper.readValue(orderString, OrderLockedEvent.class);
        log.info("Receive order locked message: {}", orderLockedEvent);

        orderService.changeStatus(orderLockedEvent.getOrderId(), OrderStatus.CONFIRM);
    }
}
