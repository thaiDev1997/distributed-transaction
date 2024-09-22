package com.order.distributed.transaction.entity;

import com.common.distributed.transaction.constant.status.DeliveryStatus;
import com.common.distributed.transaction.constant.status.OrderStatus;
import com.common.distributed.transaction.constant.status.PaymentStatus;
import com.common.distributed.transaction.constant.status.StockStatus;
import com.common.distributed.transaction.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "`order`")
public class Order extends BaseEntity {

	@Column(name = "user_id")
	private long userId;
	@Column(name = "product_id")
	private long productId;
	private int quantity;
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	// these status to handle compensating actions
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status")
	private PaymentStatus paymentStatus;
	@Enumerated(EnumType.STRING)
	@Column(name = "stock_status")
	private StockStatus stockStatus;
	@Enumerated(EnumType.STRING)
	@Column(name = "delivery_status")
	private DeliveryStatus deliveryStatus;

}
