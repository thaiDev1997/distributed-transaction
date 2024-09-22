package com.common.distributed.transaction.dto.event;

import com.common.distributed.transaction.constant.status.StockStatus;
import com.common.distributed.transaction.dto.RequestOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class StockEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    RequestOrder order;
    StockStatus status;
}
