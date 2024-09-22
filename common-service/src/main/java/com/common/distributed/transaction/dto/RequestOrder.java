package com.common.distributed.transaction.dto;

import com.common.distributed.transaction.constant.PaymentMode;
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

@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
public class RequestOrder implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private long userId;
	private long productId;
	private int quantity;
	PaymentMode paymentMode;

	private long orderId; // after order created
	private double amount; // after quantity * product_amount
	String address; // after payment created
	String phone; // after payment created

}
