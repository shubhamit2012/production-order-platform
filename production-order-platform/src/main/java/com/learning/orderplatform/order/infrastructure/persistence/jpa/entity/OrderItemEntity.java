package com.learning.orderplatform.order.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
public class OrderItemEntity {

    private UUID productId;
    private int quantity;

    public OrderItemEntity() {
    }

    public OrderItemEntity(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
