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
import vn.test.product_service.dto.request.DeductStockReq;
import vn.test.product_service.dto.request.ProductFilter;
import vn.test.product_service.entity.Product;
import vn.test.product_service.service.ProductService;

import java.util.List;

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

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<List<Product>>> search(@RequestBody ProductFilter productFilter) {
        List<Product> products = productService.search(productFilter);
        return ResponseEntity.ok(new BaseResponse<>(products, "success" ));
    }

    @PostMapping("/deduct-stock")
    public ResponseEntity<BaseResponse<Void>> deductStock(@RequestBody @Valid List<DeductStockReq> deductStockReqs) {
        productService.deductStock(deductStockReqs);
        return ResponseEntity.ok(new BaseResponse<>(null, "success"));
    }
}
