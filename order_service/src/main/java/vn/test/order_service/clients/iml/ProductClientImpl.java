package vn.test.order_service.clients.iml;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import vn.test.order_service.clients.ProductClient;
import vn.test.order_service.dto.request.DeductStockReq;
import vn.test.order_service.dto.request.ProductFilter;
import vn.test.order_service.dto.BaseResponse;
import vn.test.order_service.dto.ProductDTO;
import vn.test.order_service.exception.ApplicationException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {

    private final WebClient.Builder webClientBuilder;

    public List<ProductDTO> getProductsByIds(ProductFilter productFilter) {
        BaseResponse<List<ProductDTO>> response = webClientBuilder.build()
                .post()
                .uri("http://localhost:8080/v1/products/search")
                .bodyValue(productFilter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<List<ProductDTO>>>() {})
                .block();

        if (response == null || response.getData() == null) {
            throw new ApplicationException("Không thể lấy thông tin sản phẩm từ Product Service");
        }

        return response.getData();
    }

    @Override
    public void deductStock(List<DeductStockReq> deductStockReqs) {
        BaseResponse<Void> response = webClientBuilder.build()
                .post()
                .uri("http://localhost:8080/v1/products/deduct-stock")
                .bodyValue(deductStockReqs)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<Void>>() {})
                .block();

        if (response == null) {
            throw new ApplicationException("Can not deduct stock from Product Service");
        }
    }
}
