package com.stock.distributed.transaction.jms.listener;

import com.common.distributed.transaction.constant.TopicName;
import com.common.distributed.transaction.constant.status.OrderStatus;
import com.common.distributed.transaction.constant.status.StockStatus;
import com.common.distributed.transaction.dto.event.OrderEvent;
import com.common.distributed.transaction.dto.event.StockEvent;
import com.stock.distributed.transaction.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class StockListener {
    ProductService productService;
    KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = TopicName.ORDER_CREATED, groupId = TopicName.ORDER_CREATED + "-stock-group")
    public void orderCreated(@Payload OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, orderEvent));
        if (OrderStatus.CREATED.equals(orderEvent.getStatus())) {
            StockEvent stockEvent;
            try {
                stockEvent = productService.reserveOrderProduct(orderEvent);
            } catch (RuntimeException e) {
                log.error(e.getMessage(), e);
                stockEvent = StockEvent.builder().order(orderEvent.getOrder()).status(StockStatus.FAILED).build();
            }
            if (StockStatus.RESERVED.equals(stockEvent.getStatus())) {
                // continue workflow
                kafkaTemplate.send(TopicName.STOCK_REVERSED, stockEvent);
            } else {
                // require to handle compensating action
                kafkaTemplate.send(TopicName.STOCK_RESERVATION_FAILED, stockEvent);
            }
        }
    }

    @KafkaListener(topics = TopicName.ORDER_CANCELLED, groupId = TopicName.ORDER_CANCELLED + "-stock-group")
    public void orderCancelled(OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, orderEvent));
        boolean released = productService.releaseOrderProduct(orderEvent);
        if (released) {
            StockEvent stockEvent = StockEvent.builder().order(orderEvent.getOrder()).status(StockStatus.RELEASED).build();
            kafkaTemplate.send(TopicName.STOCK_RELEASED, stockEvent);
        }
    }
}
