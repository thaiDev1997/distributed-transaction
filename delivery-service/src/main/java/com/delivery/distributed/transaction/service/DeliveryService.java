package com.delivery.distributed.transaction.service;

import com.common.distributed.transaction.constant.status.DeliveryStatus;
import com.common.distributed.transaction.dto.RequestOrder;
import com.common.distributed.transaction.dto.event.DeliveryEvent;
import com.common.distributed.transaction.dto.event.OrderEvent;
import com.common.distributed.transaction.dto.event.PaymentEvent;
import com.delivery.distributed.transaction.entity.Delivery;
import com.delivery.distributed.transaction.repository.DeliveryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DeliveryService {
    DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryEvent paymentProcessed(PaymentEvent paymentEvent) {
        RequestOrder requestOrder = paymentEvent.getOrder();
        String address = requestOrder.getAddress();
        String phone = requestOrder.getPhone();
        if (Objects.isNull(address) || Objects.isNull(phone)) {
            return DeliveryEvent.builder().order(requestOrder).status(DeliveryStatus.FAILED).build();
        }
        Delivery delivery = Delivery.of(requestOrder.getOrderId(), address, phone, DeliveryStatus.SCHEDULED);
        deliveryRepository.save(delivery);
        return DeliveryEvent.builder().order(requestOrder).status(DeliveryStatus.SCHEDULED).build();
    }

    @Transactional
    public void orderCancelled(OrderEvent orderEvent) {
        deliveryRepository
                .findByOrderId(orderEvent.getOrder().getOrderId())
                .filter(delivery -> DeliveryStatus.SCHEDULED.equals(delivery.getStatus()))
                .ifPresent(delivery -> {
                    delivery.setStatus(DeliveryStatus.CANCELLED);
                    deliveryRepository.save(delivery);
                });
    }

    @Transactional
    public boolean updateOrderStatus(Long orderId, DeliveryStatus deliveryStatus) {
        return deliveryRepository
                .findByOrderId(orderId)
                // DELIVERED & CANCELLED can't update status
                .filter(delivery -> !delivery.getStatus().equals(deliveryStatus)
                        && !DeliveryStatus.DELIVERED.equals(delivery.getStatus())
                        && !DeliveryStatus.CANCELLED.equals(delivery.getStatus()))
                .map(delivery -> {
                    delivery.setStatus(deliveryStatus);
                    deliveryRepository.save(delivery);
                    return true;
                })
                .orElse(false);
    }
}
