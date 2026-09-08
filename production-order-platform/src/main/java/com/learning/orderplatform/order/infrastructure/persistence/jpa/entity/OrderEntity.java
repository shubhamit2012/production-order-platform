package com.learning.orderplatform.order.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
public class OrderEntity {

    private UUID id;
    private UUID customerId;
    private String status;
    private List<OrderItemEntity> items;
    private Instant createdAt;

    public OrderEntity() {
    }

    public OrderEntity(UUID id,
                       UUID customerId,
                       String status,
                       List<OrderItemEntity> items,
                       Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.items = items;
        this.createdAt = createdAt;
    }

}
