package com.learning.orderservice.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private String status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> items;

    @Column(nullable = false)
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
