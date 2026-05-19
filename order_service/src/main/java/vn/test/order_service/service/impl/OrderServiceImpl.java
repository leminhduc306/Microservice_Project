package vn.test.order_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.test.order_service.dto.request.OrderReq;
import vn.test.order_service.dto.response.OrderRes;
import vn.test.order_service.entity.Order;
import vn.test.order_service.exception.ApplicationException;
import vn.test.order_service.mapper.OrderMapper;
import vn.test.order_service.repository.OrderRepository;
import vn.test.order_service.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderRes create(OrderReq orderReq) {
        Order order = orderMapper.toEntity(orderReq);
        int totalAmount = 0;
        if (order.getItems() != null) {
            final Order finalOrder = order;
            for (var item : order.getItems()) {
                item.setOrder(finalOrder);
                int price = item.getPrice() != null ? item.getPrice() : 0;
                int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                totalAmount += price * quantity;
            }
        }
        order.setTotalAmount(totalAmount);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderRes getById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Order not found with id: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderRes> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderRes update(String id, OrderReq orderReq) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Order not found with id: " + id));
        
        Order updatedData = orderMapper.toEntity(orderReq);
        existingOrder.setCustomerId(updatedData.getCustomerId());
        existingOrder.setStatus(updatedData.getStatus());
        
        existingOrder.getItems().clear();
        int totalAmount = 0;
        if (updatedData.getItems() != null) {
            final Order finalExistingOrder = existingOrder;
            for (var item : updatedData.getItems()) {
                item.setOrder(finalExistingOrder);
                finalExistingOrder.getItems().add(item);
                
                int price = item.getPrice() != null ? item.getPrice() : 0;
                int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                totalAmount += price * quantity;
            }
        }
        existingOrder.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(existingOrder);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!orderRepository.existsById(id)) {
            throw new ApplicationException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
}
