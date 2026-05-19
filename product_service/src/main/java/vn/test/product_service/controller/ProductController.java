package vn.test.product_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.test.product_service.dto.BaseResponse;
import vn.test.product_service.dto.request.CreateProductReq;
import vn.test.product_service.entity.Product;
import vn.test.product_service.service.ProductService;

@Slf4j
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<BaseResponse<Product>> create(@RequestBody @Valid CreateProductReq createProductReq) {
        return ResponseEntity.ok(new BaseResponse<>(productService.create(createProductReq), "success" ));
    }
}
