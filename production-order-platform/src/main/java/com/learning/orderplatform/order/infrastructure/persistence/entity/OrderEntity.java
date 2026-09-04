package com.learning.orderplatform.order.infrastructure.persistence.entity;

import com.learning.orderplatform.order.domain.CustomerId;
import com.learning.orderplatform.order.domain.OrderId;
import com.learning.orderplatform.order.domain.OrderItem;
import com.learning.orderplatform.order.domain.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class OrderEntity {

    private UUID orderId;
    private UUID customerId;
    private String orderStatus;
    private List<OrderItemEntity> orderItems;
    private Instant createdAt;

    public OrderEntity(UUID orderId,
                       UUID customerId,
                       String orderStatus,
                       List<OrderItemEntity> orderItems,
                       Instant createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
        this.createdAt = createdAt;
    }

}
