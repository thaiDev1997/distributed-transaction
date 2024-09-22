package com.common.distributed.transaction.constant;

public class TopicName {

    public static final String ORDER_CREATED = "OrderCreated";
    public static final String ORDER_CANCELLED = "OrderCancelled"; // compensating actions, logging, notify customers
    public static final String ORDER_PROCESSING = "OrderProcessing"; // logging, notify customers
    public static final String ORDER_COMPLETED = "OrderCompleted"; // logging, notify customers
    public static final String STOCK_REVERSED = "StockReserved";
    public static final String STOCK_RESERVATION_FAILED = "StockReservationFailed";
    public static final String STOCK_RELEASED = "StockReleased";
    public static final String PAYMENT_PROCESSED = "PaymentProcessed";
    public static final String PAYMENT_FAILED = "PaymentFailed";
    public static final String PAYMENT_REFUNDED = "PaymentRefunded";
    public static final String DELIVERY_SCHEDULED = "DeliveryScheduled";
    public static final String DELIVERY_FAILED = "DeliveryFailed";
    public static final String DELIVERY_SHIPPING_STATUS_UPDATED = "DeliveryShippingStatusUpdated";

}
