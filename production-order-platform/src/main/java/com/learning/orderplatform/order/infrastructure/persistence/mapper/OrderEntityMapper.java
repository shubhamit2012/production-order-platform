package com.learning.orderplatform.order.infrastructure.persistence.mapper;

import com.learning.orderplatform.order.domain.Order;
import com.learning.orderplatform.order.domain.OrderItem;
import com.learning.orderplatform.order.infrastructure.persistence.entity.OrderEntity;
import com.learning.orderplatform.order.infrastructure.persistence.entity.OrderItemEntity;

import java.util.List;

public class OrderEntityMapper {

    private final OrderItemEntityMapper orderItemEntityMapper;

    public OrderEntityMapper(OrderItemEntityMapper orderItemEntityMapper) {
        this.orderItemEntityMapper = orderItemEntityMapper;
    }

    public OrderEntity toEntity(Order order) {
        List<OrderItemEntity> itemEntities = orderItemEntityMapper.toEntity(order.items());
        return new OrderEntity(order.id().value(), order.customerId().value(), order.status().name(), itemEntities, order.createdAt());
    }
}
