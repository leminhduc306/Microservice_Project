package vn.test.order_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.test.order_service.dto.request.OrderItemReq;
import vn.test.order_service.dto.request.OrderReq;
import vn.test.order_service.dto.response.OrderItemRes;
import vn.test.order_service.dto.response.OrderRes;
import vn.test.order_service.entity.Order;
import vn.test.order_service.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toEntity(OrderReq orderReq);
    
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemReq orderItemReq);

    OrderRes toResponse(Order order);
    OrderItemRes toResponse(OrderItem orderItem);
}
