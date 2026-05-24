package vn.test.order_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.test.order_service.dto.BaseResponse;
import vn.test.order_service.dto.request.OrderReq;
import vn.test.order_service.dto.response.OrderRes;
import vn.test.order_service.service.OrderService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<BaseResponse<OrderRes>> create(@RequestBody @Valid OrderReq orderReq) {
        return ResponseEntity.ok(new BaseResponse<>(orderService.create(orderReq), "success"));
    }

}
