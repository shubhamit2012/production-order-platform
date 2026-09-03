package com.learning.orderplatform.order.api;

import com.learning.orderplatform.order.application.model.CreateOrderItem;
import com.learning.orderplatform.order.domain.CustomerId;

import java.time.Instant;
import java.util.List;

public class CreateOrderRequest {

    private CustomerId customerId;
    private List<CreateOrderItem> orderItems;
    private Instant createdAt;

    public CreateOrderRequest(CustomerId customerId, List<CreateOrderItem> orderItems, Instant createdAt) {
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.createdAt = createdAt;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public List<CreateOrderItem> orderItems() {
        return orderItems;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
