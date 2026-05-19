package vn.test.order_service.service;

import vn.test.order_service.dto.request.OrderReq;
import vn.test.order_service.dto.response.OrderRes;
import java.util.List;

public interface OrderService {
    OrderRes create(OrderReq orderReq);
    OrderRes getById(String id);
    List<OrderRes> getAll();
    OrderRes update(String id, OrderReq orderReq);
    void delete(String id);
}
