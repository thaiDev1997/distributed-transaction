package com.order.distributed.transaction.jms.listener;

import com.common.distributed.transaction.constant.TopicName;
import com.common.distributed.transaction.constant.status.OrderStatus;
import com.common.distributed.transaction.dto.RequestOrder;
import com.common.distributed.transaction.dto.event.OrderEvent;
import com.order.distributed.transaction.entity.Order;
import com.order.distributed.transaction.repository.OrderRepository;
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
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
@Service
public class OrderListener { // TODO: here is Orchestrator
    OrderRepository orderRepository;
    KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = TopicName.ORDER_CREATED, groupId = TopicName.ORDER_CREATED + "-stock-group")
    public void orderCreated(@Payload OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, orderEvent));
        // send "ReserveStock" command
        kafkaTemplate.send(TopicName.STOCK_RESERVE_COMMAND, orderEvent);
    }

    @KafkaListener(topics = TopicName.ORDER_PROCESSING, groupId = TopicName.ORDER_PROCESSING + "-notify-group")
    public void orderProcessing(@Payload OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        // notify user
        log.info("Order[" + orderEvent.getOrder().getOrderId() + "] status is " + TopicName.ORDER_PROCESSING);
    }

    @KafkaListener(topics = TopicName.ORDER_COMPLETED, groupId = TopicName.ORDER_COMPLETED + "-notify-group")
    public void orderCompleted(@Payload OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        // notify user
        log.info("Order[" + orderEvent.getOrder().getOrderId() + "] status is " + TopicName.ORDER_COMPLETED);
    }

    @KafkaListener(topics = TopicName.ORDER_CANCELLED, groupId = TopicName.ORDER_CANCELLED + "-notify-group")
    public void orderCancelled(@Payload OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        // notify user
        log.info("Order[" + orderEvent.getOrder().getOrderId() + "] status is " + TopicName.ORDER_CANCELLED);
    }

    private void handleOrder(Order order, OrderStatus orderStatus, RequestOrder requestOrder, String topicName) {
        order.setStatus(orderStatus);
        OrderEvent orderEvent = OrderEvent.builder()
                .order(requestOrder)
                .status(orderStatus)
                .build();
        kafkaTemplate.send(topicName, orderEvent);
    }

    protected void orderProcessing(Order order, RequestOrder requestOrder) {
        this.handleOrder(order, OrderStatus.PROCESSING, requestOrder, TopicName.ORDER_PROCESSING);
    }

    protected void orderCancelled(Order order, RequestOrder requestOrder) {
        this.handleOrder(order, OrderStatus.CANCELLED, requestOrder, TopicName.ORDER_CANCELLED);
    }

    protected void orderCompleted(Order order, RequestOrder requestOrder) {
        this.handleOrder(order, OrderStatus.COMPLETED, requestOrder, TopicName.ORDER_COMPLETED);
    }
}
