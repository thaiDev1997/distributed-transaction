package com.order.distributed.transaction.jms.listener;

import com.common.distributed.transaction.constant.TopicName;
import com.common.distributed.transaction.constant.status.DeliveryStatus;
import com.common.distributed.transaction.dto.RequestOrder;
import com.common.distributed.transaction.dto.event.DeliveryEvent;
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
public class OrderDeliveryListener extends OrderListener {

    public OrderDeliveryListener(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        super(orderRepository, kafkaTemplate);
    }

    @Transactional
    @KafkaListener(topics = TopicName.DELIVERY_SCHEDULED, groupId = TopicName.DELIVERY_SCHEDULED + "-group")
    public void deliveryScheduled(@Payload DeliveryEvent deliveryEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, deliveryEvent));
        orderRepository
                .findById(deliveryEvent.getOrder().getOrderId())
                .ifPresent(order -> {
                    order.setDeliveryStatus(DeliveryStatus.SCHEDULED);
                    orderProcessing(order, deliveryEvent.getOrder());
                });
    }

    // require compensable action
    @Transactional
    @KafkaListener(topics = TopicName.DELIVERY_FAILED, groupId = TopicName.DELIVERY_FAILED + "-group")
    public void deliveryFailed(@Payload DeliveryEvent deliveryEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, deliveryEvent));
        RequestOrder requestOrder = deliveryEvent.getOrder();
        orderRepository
                .findById(requestOrder.getOrderId())
                .ifPresent(order -> {
                    order.setDeliveryStatus(DeliveryStatus.FAILED);
                    orderCancelled(order, requestOrder);
                });
    }

    @Transactional
    @KafkaListener(topics = TopicName.DELIVERY_SHIPPING_STATUS_UPDATED, groupId = TopicName.DELIVERY_SHIPPING_STATUS_UPDATED + "-group")
    public void deliveryShippingStatusUpdated(@Payload DeliveryEvent deliveryEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, deliveryEvent));
        RequestOrder requestOrder = deliveryEvent.getOrder();
        DeliveryStatus deliveryStatus = deliveryEvent.getStatus();
        orderRepository
                .findById(requestOrder.getOrderId())
                .ifPresent(order -> {
                    order.setDeliveryStatus(deliveryStatus);

                    switch (deliveryStatus) {
                        case DeliveryStatus.DELIVERED:
                            orderCompleted(order, requestOrder);
                            break;
                        case DeliveryStatus.CANCELLED:
                            orderCancelled(order, requestOrder);
                            break;
                    }
                });
    }

}
