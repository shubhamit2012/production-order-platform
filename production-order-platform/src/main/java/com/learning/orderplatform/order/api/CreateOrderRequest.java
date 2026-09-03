package com.learning.orderplatform.order.api;

import com.learning.orderplatform.order.application.model.CreateOrderItem;

import java.time.Instant;
import java.util.List;

public class CreateOrderRequest {

    private String customerId;
    private List<CreateOrderItem> items;
    private Instant createdAt;

    public CreateOrderRequest(String customerId, List<CreateOrderItem> items, Instant createdAt) {
        this.customerId = customerId;
        this.items = items;
        this.createdAt = createdAt;
    }

    public String customerId() {
        return customerId;
    }

    public List<CreateOrderItem> orderItems() {
        return items;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
