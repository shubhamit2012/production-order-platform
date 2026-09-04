package com.learning.orderplatform.order.infrastructure.persistence.entity;

import com.learning.orderplatform.order.domain.ProductId;

import java.util.UUID;

public class OrderItemEntity {

    private UUID productId;
    private int quantity;

    public OrderItemEntity(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
