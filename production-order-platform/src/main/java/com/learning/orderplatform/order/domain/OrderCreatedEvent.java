package com.learning.orderplatform.order.domain;

import java.time.Instant;

public class OrderCreatedEvent implements DomainEvent {

    private final OrderId orderId;
    private final CustomerId customerId;
    private final Instant createdAt;

    public OrderCreatedEvent(OrderId orderId, CustomerId customerId, Instant createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.createdAt = createdAt;
    }

}
