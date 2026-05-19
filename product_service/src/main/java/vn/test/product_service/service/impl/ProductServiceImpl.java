package vn.test.product_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.test.product_service.dto.request.CreateProductReq;
import vn.test.product_service.entity.Product;
import vn.test.product_service.exception.ApplicationException;
import vn.test.product_service.mapper.ProductMapper;
import vn.test.product_service.repository.CategoryRepository;
import vn.test.product_service.repository.ProductRepository;
import vn.test.product_service.service.ProductService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public Product create(CreateProductReq createProductReq) {
        var existingCategory = categoryRepository.findById(createProductReq.getCategoryId());
        if (existingCategory.isEmpty()) {
            throw new ApplicationException("category not found");
        }

        Product product = productMapper.fromCreateProductReq(createProductReq);
        product.setIsDeleted(false);
        return productRepository.save(product);
    }
}
