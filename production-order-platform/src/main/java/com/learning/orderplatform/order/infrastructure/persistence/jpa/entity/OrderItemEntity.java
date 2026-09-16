package com.learning.orderplatform.order.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private int quantity;

    public OrderItemEntity() {
    }

    public OrderItemEntity(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
