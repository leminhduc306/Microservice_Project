package vn.test.product_service.service;

import vn.test.product_service.dto.request.CreateProductReq;
import vn.test.product_service.dto.request.DeductStockReq;
import vn.test.product_service.dto.request.ProductFilter;
import vn.test.product_service.entity.Product;

import java.util.List;

public interface ProductService {
    Product create (CreateProductReq createProductReq);
    List<Product> search(ProductFilter productFilter);
    void deductStock(List<DeductStockReq> deductStockReqs);
}
