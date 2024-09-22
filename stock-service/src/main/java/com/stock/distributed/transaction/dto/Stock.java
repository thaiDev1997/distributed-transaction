package com.stock.distributed.transaction.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class Stock implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private long productId;
	private String productName;

	private int quantity;
	private double unitPrice;

}
