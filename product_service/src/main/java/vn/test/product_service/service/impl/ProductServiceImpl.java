package vn.test.product_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final RedissonClient redissonClient;

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

    @Override
    @Transactional
    public void lockProduct(LockProductReq lockProductReq) {
        List<LockProductItem> items = lockProductReq.getItems();

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Lock product items must not be empty");
        }

        List<String> sortedIds = items.stream()
                .map(LockProductItem::getId)
                .sorted()
                .toList();

        List<RLock> locks = sortedIds.stream()
                .map(id -> redissonClient.getLock("lock:product:" + id))
                .toList();

        RLock multiLock = redissonClient.getMultiLock(
                locks.toArray(new RLock[0])
        );

        boolean locked = false;

        try {
            locked = multiLock.tryLock(10, 30, TimeUnit.SECONDS);

            if (!locked) {
                throw new ApplicationException("Server busy, please try again later");
            }

            log.info("Acquired Redis lock for [{}]", locks);
            Thread.sleep(3000);
            Map<String, Integer> productIdQuantityMap = items.stream()
                    .collect(Collectors.toMap(
                            LockProductItem::getId,
                            LockProductItem::getQuantity
                    ));

            List<Product> products = productRepository.findByIdIn(
                    new ArrayList<>(productIdQuantityMap.keySet())
            );

            if (products.isEmpty()) {
                throw new ApplicationException("Product not found");
            }

            if (products.size() != productIdQuantityMap.size()) {
                throw new ApplicationException("Some products were not found");
            }

            products.forEach(product -> {
                Integer quantity = productIdQuantityMap.get(product.getId());

                if (quantity == null || quantity <= 0) {
                    throw new ApplicationException("Invalid quantity for product " + product.getId());
                }

                int remainStock = product.getStock() - quantity;

                if (remainStock < 0) {
                    throw new ApplicationException(
                            "Product " + product.getId() + " is out of stock"
                    );
                }
                product.setStock(remainStock);
            });
            productRepository.saveAll(products);

            log.info("Updated stock successfully for products [{}]", sortedIds);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Process interrupted", e);

        } finally {
            log.info("Waiting for unlock products [{}]", sortedIds);

            if (locked) {
                multiLock.unlock();
                log.info("Unlock success for products [{}]", sortedIds);
            }
        }
    }
}
