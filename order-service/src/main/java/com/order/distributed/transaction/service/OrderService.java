package com.order.distributed.transaction.service;

import com.common.distributed.transaction.constant.status.OrderStatus;
import com.common.distributed.transaction.constant.status.PaymentStatus;
import com.common.distributed.transaction.dto.RequestOrder;
import com.order.distributed.transaction.entity.Order;
import com.order.distributed.transaction.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class OrderService {
    OrderRepository orderRepository;

    @Transactional
    public long createOrder(RequestOrder requestOrder) {
        Order order = Order.builder()
                .userId(requestOrder.getUserId())
                .productId(requestOrder.getProductId())
                .quantity(requestOrder.getQuantity())
                .status(OrderStatus.CREATED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        order = orderRepository.saveAndFlush(order);
        return order.getId();
    }

    @Transactional
    public void reverseOrder(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        order.ifPresent(o -> {
            o.setStatus(OrderStatus.CANCELLED);
            this.orderRepository.save(o);
        });
    }
}
