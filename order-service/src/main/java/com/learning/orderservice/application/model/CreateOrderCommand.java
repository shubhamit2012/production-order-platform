package com.learning.orderservice.application.model;

import com.learning.orderservice.domain.CustomerId;

import java.time.Instant;
import java.util.List;

public record CreateOrderCommand(
        CustomerId customerId,
        List<CreateOrderItem> items,
        Instant createdAt) {

}
