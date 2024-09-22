package com.payment.distributed.transaction.jms.listener;

import com.common.distributed.transaction.constant.TopicName;
import com.common.distributed.transaction.constant.status.PaymentStatus;
import com.common.distributed.transaction.dto.event.OrderEvent;
import com.common.distributed.transaction.dto.event.PaymentEvent;
import com.common.distributed.transaction.dto.event.StockEvent;
import com.payment.distributed.transaction.service.PaymentService;
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
public class PaymentListener {
    PaymentService paymentService;
    KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = TopicName.STOCK_REVERSED, groupId = TopicName.STOCK_REVERSED + "-payment-group")
    public void orderCreated(@Payload StockEvent stockEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, stockEvent));
        PaymentEvent paymentEvent;
        try {
            paymentEvent = paymentService.stockReserved(stockEvent);
        } catch (RuntimeException e) {
            paymentEvent = PaymentEvent.builder().order(stockEvent.getOrder()).status(PaymentStatus.FAILED).build();
        }
        if (PaymentStatus.PROCESSED.equals(paymentEvent.getStatus())) {
            // continue workflow
            kafkaTemplate.send(TopicName.PAYMENT_PROCESSED, paymentEvent);
        } else {
            // require to handle compensating action
            kafkaTemplate.send(TopicName.PAYMENT_FAILED, paymentEvent);
        }
    }

    @KafkaListener(topics = TopicName.ORDER_CANCELLED, groupId = TopicName.ORDER_CANCELLED + "-payment-group")
    public void orderCancelled(@Payload OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(String.format("Topic: %s - Payload: %s", topic, orderEvent));
        boolean refunded = paymentService.refundPayment(orderEvent);
        if (refunded) {
            PaymentEvent paymentEvent = PaymentEvent.builder().order(orderEvent.getOrder()).status(PaymentStatus.REFUNDED).build();
            kafkaTemplate.send(TopicName.PAYMENT_REFUNDED, paymentEvent);
        }
    }
}
