package com.delivery.distributed.transaction.service;

import com.common.distributed.transaction.constant.status.DeliveryStatus;
import com.common.distributed.transaction.dto.RequestOrder;
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
