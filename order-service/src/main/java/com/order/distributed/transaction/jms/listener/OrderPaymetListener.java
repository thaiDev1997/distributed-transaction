package com.order.distributed.transaction.jms.listener;

import com.common.distributed.transaction.constant.TopicName;
import com.common.distributed.transaction.constant.status.PaymentStatus;
import com.common.distributed.transaction.dto.RequestOrder;
import com.common.distributed.transaction.dto.event.PaymentEvent;
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
public class OrderPaymetListener extends OrderListener {

    public OrderPaymetListener(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        super(orderRepository, kafkaTemplate);
    }

    @Transactional
    @KafkaListener(topics = TopicName.PAYMENT_PROCESSED, groupId = TopicName.PAYMENT_PROCESSED + "-group")
    public void paymentProcessed(@Payload PaymentEvent paymentEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, paymentEvent));
        orderRepository
                .findById(paymentEvent.getOrder().getOrderId())
                .ifPresent(order -> {
                    order.setPaymentStatus(PaymentStatus.PROCESSED);
                    kafkaTemplate.send(TopicName.DELIVERY_SCHEDULE_COMMAND, paymentEvent);
                });
    }

    // require compensable action
    @Transactional
    @KafkaListener(topics = TopicName.PAYMENT_FAILED, groupId = TopicName.PAYMENT_FAILED + "-group")
    public void paymentFailed(@Payload PaymentEvent paymentEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, paymentEvent));
        RequestOrder requestOrder = paymentEvent.getOrder();
        orderRepository
                .findById(requestOrder.getOrderId())
                .ifPresent(order -> {
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    orderCancelled(order, requestOrder);
                });
    }

    @Transactional
    @KafkaListener(topics = TopicName.PAYMENT_REFUNDED, groupId = TopicName.PAYMENT_REFUNDED + "-group")
    public void paymentRefunded(@Payload PaymentEvent paymentEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, paymentEvent));
        RequestOrder requestOrder = paymentEvent.getOrder();
        orderRepository
                .findById(requestOrder.getOrderId())
                .ifPresent(order -> {
                    order.setPaymentStatus(PaymentStatus.REFUNDED);
                });
    }

}
