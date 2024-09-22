package com.delivery.distributed.transaction.jms.listener;

import com.common.distributed.transaction.constant.TopicName;
import com.common.distributed.transaction.constant.status.DeliveryStatus;
import com.common.distributed.transaction.dto.event.DeliveryEvent;
import com.common.distributed.transaction.dto.event.OrderEvent;
import com.common.distributed.transaction.dto.event.PaymentEvent;
import com.delivery.distributed.transaction.service.DeliveryService;
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
public class DeliveryListener {
    DeliveryService deliveryService;
    KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = TopicName.PAYMENT_PROCESSED, groupId = TopicName.PAYMENT_PROCESSED + "-delivery-group")
    public void paymentProcessed(@Payload PaymentEvent paymentEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, paymentEvent));
        DeliveryEvent deliveryEvent;
        try {
            deliveryEvent = deliveryService.paymentProcessed(paymentEvent);
        } catch (RuntimeException e) {
            deliveryEvent = DeliveryEvent.builder().order(paymentEvent.getOrder()).status(DeliveryStatus.FAILED).build();
        }
        if (DeliveryStatus.SCHEDULED.equals(deliveryEvent.getStatus())) {
            // continue workflow
            kafkaTemplate.send(TopicName.DELIVERY_SCHEDULED, deliveryEvent);
        } else {
            // require to handle compensating action
            kafkaTemplate.send(TopicName.DELIVERY_FAILED, deliveryEvent);
        }
    }

    @KafkaListener(topics = TopicName.ORDER_CANCELLED, groupId = TopicName.ORDER_CANCELLED + "-delivery-group")
    public void orderCancelled(OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, orderEvent));
        deliveryService.orderCancelled(orderEvent);
    }
}
