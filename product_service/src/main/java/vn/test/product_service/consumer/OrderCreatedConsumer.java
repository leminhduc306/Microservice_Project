package vn.test.product_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.test.product_service.consumer.dto.Order;

@Component
@Slf4j
public class OrderCreatedConsumer {

    @KafkaListener(topics = "order_created")
    public void handleOrderCreated(String orderString) throws JsonProcessingException {
        log.info("Received order message: {}" , orderString);
        ObjectMapper objectMapper = new ObjectMapper();
        Order order = objectMapper.readValue(orderString, Order.class);
        log.info("Received order: {}", order);
    }
}
