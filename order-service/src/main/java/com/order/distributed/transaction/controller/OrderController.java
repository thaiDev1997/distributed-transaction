package com.order.distributed.transaction.controller;

import com.common.distributed.transaction.constant.TopicName;
import com.common.distributed.transaction.constant.status.OrderStatus;
import com.common.distributed.transaction.dto.RequestOrder;
import com.common.distributed.transaction.dto.event.OrderEvent;
import com.order.distributed.transaction.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    OrderService orderService;
    KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> placeOrder(@RequestBody RequestOrder requestOrder) {
        long orderId = orderService.createOrder(requestOrder);
        requestOrder.setOrderId(orderId);

        OrderEvent orderEvent = OrderEvent.builder()
                .order(requestOrder)
                .status(OrderStatus.CREATED)
                .build();
        kafkaTemplate.send(TopicName.ORDER_CREATED, orderEvent);

        return ResponseEntity.ok(Map.of(
                "message", "Order created"
        ));
    }
}
