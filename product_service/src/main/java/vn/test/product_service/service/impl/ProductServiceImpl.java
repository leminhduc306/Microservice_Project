package vn.test.product_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.test.product_service.dto.request.CreateProductReq;
import vn.test.product_service.dto.request.DeductStockReq;
import vn.test.product_service.dto.request.ProductFilter;
import vn.test.product_service.entity.Product;
import vn.test.product_service.exception.ApplicationException;
import vn.test.product_service.mapper.ProductMapper;
import vn.test.product_service.repository.CategoryRepository;
import vn.test.product_service.repository.ProductRepository;
import vn.test.product_service.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public Product create(CreateProductReq createProductReq) {
        var existingCategory = categoryRepository.findById(createProductReq.getCategoryId());
        if (existingCategory.isEmpty()) {
            throw new ApplicationException("category not found");
        }

        Product product = productMapper.fromCreateProductReq(createProductReq);
        product.setIsDeleted(false);
        return productRepository.save(product);
    }

    @Override
    public List<Product> search(ProductFilter productFilter) {
        return productRepository.findByIdIn(productFilter.getIds());
    }

    @Override
    @Transactional
    public void deductStock(List<DeductStockReq> deductStockReqs) {
        List<String> productIds = deductStockReqs.stream()
                .map(DeductStockReq::getProductId)
                .distinct()
                .toList();

        List<Product> products = productRepository.findByIdIn(productIds);
        Map<String, Product> productMap = new HashMap<>();
        products.forEach(product -> productMap.put(product.getId(), product));

        for (DeductStockReq deductStockReq : deductStockReqs) {
            Product product = productMap.get(deductStockReq.getProductId());
            if (product == null) {
                throw new ApplicationException("product not found: " + deductStockReq.getProductId());
            }

            if (product.getStock() < deductStockReq.getQuantity()) {
                throw new ApplicationException("product not enough stock: " + deductStockReq.getProductId());
            }
            product.setStock(product.getStock() - deductStockReq.getQuantity());
        }

        productRepository.saveAll(products);
    }
}
