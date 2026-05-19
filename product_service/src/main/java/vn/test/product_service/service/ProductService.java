package vn.test.product_service.service;

import vn.test.product_service.dto.request.CreateProductReq;
import vn.test.product_service.entity.Product;

public interface ProductService {
    Product create (CreateProductReq createProductReq);
}
