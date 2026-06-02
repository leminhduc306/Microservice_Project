package vn.test.product_service.service;

import vn.test.product_service.dto.request.CreateProductReq;
import vn.test.product_service.dto.request.LockProductReq;
import vn.test.product_service.dto.request.ProductFilter;
import vn.test.product_service.entity.Product;

import java.util.List;

public interface ProductService {
    Product create (CreateProductReq createProductReq);
    List<Product> search(ProductFilter productFilter);
    void lock(LockProductReq lockProductReq);
    void lockProduct(LockProductReq lockProductReq);
}
