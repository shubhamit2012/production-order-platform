package com.learning.orderplatform.order.infrastructure.persistence.mapper;

import com.learning.orderplatform.order.domain.OrderItem;
import com.learning.orderplatform.order.infrastructure.persistence.entity.OrderItemEntity;

import java.util.List;

public class OrderItemEntityMapper {

    public List<OrderItemEntity> toEntity(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> new OrderItemEntity(item.productId().value(), item.quantity()))
                .toList();
    }

}
