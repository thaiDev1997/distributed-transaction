package com.order.distributed.transaction.jms.listener;

import com.common.distributed.transaction.constant.TopicName;
import com.common.distributed.transaction.constant.status.StockStatus;
import com.common.distributed.transaction.dto.RequestOrder;
import com.common.distributed.transaction.dto.event.StockEvent;
import com.order.distributed.transaction.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderStockListener extends OrderListener {

    public OrderStockListener(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        super(orderRepository, kafkaTemplate);
    }

    @Transactional
    @KafkaListener(topics = TopicName.STOCK_REVERSED, groupId = TopicName.STOCK_REVERSED + "-order-group")
    public void stockReversed(@Payload StockEvent stockEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, stockEvent));
        orderRepository
                .findById(stockEvent.getOrder().getOrderId())
                .ifPresent(order -> {
                    order.setStockStatus(StockStatus.RESERVED);
                    kafkaTemplate.send(TopicName.PAYMENT_PROCESS_COMMAND, stockEvent);
                });
    }

    // require compensable action
    @Transactional
    @KafkaListener(topics = TopicName.STOCK_RESERVATION_FAILED, groupId = TopicName.STOCK_RESERVATION_FAILED + "-group")
    public void stockReservationFailed(@Payload StockEvent stockEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, stockEvent));
        RequestOrder requestOrder = stockEvent.getOrder();
        orderRepository
                .findById(requestOrder.getOrderId())
                .ifPresent(order -> {
                    order.setStockStatus(StockStatus.FAILED);
                    orderCancelled(order, requestOrder);
                });
    }

    @Transactional
    @KafkaListener(topics = TopicName.STOCK_RELEASED, groupId = TopicName.STOCK_RELEASED + "-group")
    public void stockReleased(@Payload StockEvent stockEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, stockEvent));
        RequestOrder requestOrder = stockEvent.getOrder();
        orderRepository
                .findById(requestOrder.getOrderId())
                .ifPresent(order -> {
                    order.setStockStatus(StockStatus.RELEASED);
                });
    }

}
