package com.learning.orderservice.infrastructure.persistence.mapper;

import com.learning.orderservice.domain.OrderItem;
import com.learning.orderservice.infrastructure.persistence.jpa.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemEntityMapper {

    public List<OrderItemEntity> toEntity(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> new OrderItemEntity(item.productId().value(), item.quantity()))
                .toList();
    }

}
