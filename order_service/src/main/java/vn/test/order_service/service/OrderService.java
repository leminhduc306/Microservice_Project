package vn.test.order_service.service;

import vn.test.order_service.dto.request.OrderReq;
import vn.test.order_service.dto.response.OrderRes;

public interface OrderService {
    OrderRes create(OrderReq orderReq);
}
