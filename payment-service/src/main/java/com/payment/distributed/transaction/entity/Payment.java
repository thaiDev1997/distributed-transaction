package com.payment.distributed.transaction.entity;

import com.common.distributed.transaction.constant.PaymentMode;
import com.common.distributed.transaction.constant.status.PaymentStatus;
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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment extends BaseEntity {

	@Column(name = "user_id")
	long userId;
	@Column(name = "order_id")
	long orderId;
	double amount;
	@Enumerated(EnumType.STRING)
	PaymentMode mode;
	@Enumerated(EnumType.STRING)
	PaymentStatus status;

}
