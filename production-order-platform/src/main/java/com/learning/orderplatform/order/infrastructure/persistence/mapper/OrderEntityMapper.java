package com.learning.orderplatform.order.infrastructure.persistence.mapper;

import com.learning.orderplatform.order.domain.Order;
import com.learning.orderplatform.order.domain.OrderItem;
import com.learning.orderplatform.order.infrastructure.persistence.entity.OrderEntity;
import com.learning.orderplatform.order.infrastructure.persistence.entity.OrderItemEntity;

import java.util.List;

public class OrderEntityMapper {

    private List<OrderItemEntity> toOrderItemEntity(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> new OrderItemEntity(item.productId().value(), item.quantity()))
                .toList();
    }

    public OrderEntity toEntity(Order order) {
        List<OrderItemEntity> itemEntities = toOrderItemEntity(order.items());
        new OrderEntity();
        return new OrderEntity(order.id().value(), order.customerId().value(), order.status().name(), itemEntities, order.createdAt());
    }
}
