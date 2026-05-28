package vn.test.order_service.clients;

import vn.test.order_service.dto.request.ProductFilter;
import vn.test.order_service.dto.ProductDTO;

import java.util.List;

public interface ProductClient {
    public List<ProductDTO> getProductsByIds(ProductFilter productFilter) ;
}
