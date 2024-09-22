package com.stock.distributed.transaction.service;

import com.stock.distributed.transaction.dto.Stock;
import com.stock.distributed.transaction.entity.Product;
import com.stock.distributed.transaction.repository.OrderProductRepository;
import com.stock.distributed.transaction.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class ProductService {

    ProductRepository productRepository;
    OrderProductRepository orderProductRepository;

    @PostConstruct
    public void init() {
        for (int i = 1; i < 4; i++) {
            Iterable<Product> products = productRepository.findByName("Product " + i);
            Iterator<Product> iterator = products.iterator();
            if (!iterator.hasNext()) {
                Product product = Product.builder().name("Product " + i).unitPrice(i * 100.0).quantity(i * 10).build();
                productRepository.save(product);
            } else {
                while (iterator.hasNext()) {
                    Product product = iterator.next();
                    product.setQuantity(i * 10);
                    productRepository.save(product);
                }
            }
        }
    }

    @Transactional
    public void addProduct(Stock stock) {
        long productId = stock.getProductId();
        if (productId > 0) {
            productRepository
                    .findById(stock.getProductId())
                    .ifPresent(product -> {
                        product.setQuantity(product.getQuantity() + stock.getQuantity());
                        product.setUnitPrice(stock.getUnitPrice());
                        productRepository.save(product);
                    });
        } else {
            Product i = Product.builder()
                    .name(stock.getProductName())
                    .quantity(stock.getQuantity())
                    .unitPrice(stock.getUnitPrice())
                    .build();
            productRepository.save(i);
        }
    }
}
