package com.stock.distributed.transaction.service;

import com.common.distributed.transaction.constant.status.StockStatus;
import com.common.distributed.transaction.dto.RequestOrder;
import com.stock.distributed.transaction.dto.Stock;
import com.common.distributed.transaction.dto.event.OrderEvent;
import com.common.distributed.transaction.dto.event.StockEvent;
import com.stock.distributed.transaction.entity.OrderProduct;
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
import java.util.Objects;

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

    @Transactional
    public StockEvent reserveOrderProduct(OrderEvent orderEvent) {
        RequestOrder requestOrder = orderEvent.getOrder();
        long productId = requestOrder.getProductId();
        int orderQuantity = requestOrder.getQuantity();
        return productRepository.findById(requestOrder.getProductId())
                .filter(product -> product.getQuantity() > 0 && product.getQuantity() >= orderQuantity)
                .map(product -> {
                    product.setQuantity(product.getQuantity() - orderQuantity);
                    productRepository.save(product);
                    orderProductRepository.save(OrderProduct.of(requestOrder.getOrderId(), productId, orderQuantity));

                    requestOrder.setAmount(orderQuantity * product.getUnitPrice());
                    return StockEvent.builder().order(requestOrder).status(StockStatus.RESERVED).build();
                }).orElse(StockEvent.builder().order(requestOrder).status(StockStatus.FAILED).build());
    }

    @Transactional
    public boolean releaseOrderProduct(OrderEvent orderEvent) {
        RequestOrder order = orderEvent.getOrder();
        return orderProductRepository.findByOrderId(order.getOrderId())
                .map(orderProduct -> {
                    return productRepository
                            .findById(orderProduct.getProductId())
                            .map(product -> {
                                // compensable transaction
                                product.setQuantity(product.getQuantity() + orderProduct.getQuantity());
                                productRepository.save(product);
                                return true;
                            })
                            .orElse(false);
                })
                .orElse(false);
    }
}
