package vn.test.product_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.test.product_service.dto.request.CreateProductReq;
import vn.test.product_service.dto.request.LockProductItem;
import vn.test.product_service.dto.request.LockProductReq;
import vn.test.product_service.dto.request.ProductFilter;
import vn.test.product_service.entity.Product;
import vn.test.product_service.exception.ApplicationException;
import vn.test.product_service.mapper.ProductMapper;
import vn.test.product_service.repository.CategoryRepository;
import vn.test.product_service.repository.ProductRepository;
import vn.test.product_service.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return productRepository.save(product);
    }

    @Override
    public List<Product> search(ProductFilter productFilter) {
        return productRepository.findByIdIn(productFilter.getIds());
    }

    @Override
    @Transactional
    public void lock(LockProductReq lockProductReq) {
        List<LockProductItem> items = lockProductReq.getItems();

        var productIdQuantityMap = items.stream()
                .collect(Collectors.toMap(LockProductItem::getId, LockProductItem::getQuantity));

        List<Product> products = productRepository.findByIdInForUpdate(new ArrayList<>(productIdQuantityMap.keySet()));
        if (products.isEmpty()) {
            throw new ApplicationException("Product not found");
        }

        products.forEach(product -> {
            if(product.getStock() < productIdQuantityMap.get(product.getId())) {
                throw new ApplicationException("Product quantity out of stock");
            }
            product.setStock(product.getStock() - productIdQuantityMap.get(product.getId()));
        });

        productRepository.saveAll(products);
    }
}
