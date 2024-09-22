package com.delivery.distributed.transaction.entity;

import com.common.distributed.transaction.constant.status.DeliveryStatus;
import com.common.distributed.transaction.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "delivery")
public class Delivery extends BaseEntity {

	@Column(name = "order_id")
	long orderId;
	String address;
	String phone;
	@Enumerated(EnumType.STRING)
	DeliveryStatus status;
}
