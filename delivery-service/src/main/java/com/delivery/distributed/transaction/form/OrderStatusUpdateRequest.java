package com.delivery.distributed.transaction.form;

import com.common.distributed.transaction.constant.status.DeliveryStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderStatusUpdateRequest {
    DeliveryStatus status;
    LocalDateTime timestamp;
    String reason;
}
