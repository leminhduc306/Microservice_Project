package vn.test.order_service.service;

import vn.test.order_service.dto.request.OrderReq;
import vn.test.order_service.dto.response.OrderRes;
import vn.test.order_service.enums.OrderStatus;

public interface OrderService {
    OrderRes create(OrderReq orderReq);
    void changeStatus(String orderId, OrderStatus status);
}
