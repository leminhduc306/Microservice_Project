package vn.test.product_service.mapper;

import org.mapstruct.Mapper;
import vn.test.product_service.dto.request.CreateProductReq;
import vn.test.product_service.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product fromCreateProductReq (CreateProductReq createProductReq);

}
