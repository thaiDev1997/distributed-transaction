package com.delivery.distributed.transaction.controller;

import com.common.distributed.transaction.constant.status.DeliveryStatus;
import com.common.distributed.transaction.constant.TopicName;
import com.common.distributed.transaction.dto.RequestOrder;
import com.common.distributed.transaction.dto.event.DeliveryEvent;
import com.delivery.distributed.transaction.form.OrderStatusUpdateRequest;
import com.delivery.distributed.transaction.service.DeliveryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderShippingController {

    DeliveryService deliveryService;
    KafkaTemplate<String, Object> kafkaTemplate;

    @PutMapping(value = "/{orderId}/delivery-status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                               @RequestBody OrderStatusUpdateRequest request) {
        DeliveryStatus deliveryStatus = request.getStatus();
        if (!isValidStatus(deliveryStatus)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid status", "message",
                            "Status '" + request.getStatus() + "' is not valid."));
        }
        boolean updated = deliveryService.updateOrderStatus(orderId, deliveryStatus);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid status", "message",
                            "Order status updated unsuccessfully"));
        }
        log.info("Order[" + orderId + "] was updated status[" + request.getStatus().name() + "] at " + request.getTimestamp());

        RequestOrder requestOrder = RequestOrder.builder().orderId(orderId).build();
        DeliveryEvent deliveryEvent = DeliveryEvent.builder().order(requestOrder).status(deliveryStatus).build();
        kafkaTemplate.send(TopicName.DELIVERY_SHIPPING_STATUS_UPDATED, deliveryEvent);

        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "message", "Order status updated successfully"
        ));
    }

    private boolean isValidStatus(DeliveryStatus status) {
        return DeliveryStatus.DELIVERING.equals(status) ||
                DeliveryStatus.DELIVERED.equals(status) ||
                DeliveryStatus.CANCELLED.equals(status);
    }

}
