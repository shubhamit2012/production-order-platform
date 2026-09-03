package com.learning.orderplatform.order.application.model;

import com.learning.orderplatform.order.domain.CustomerId;

import java.time.Instant;
import java.util.List;

public record CreateOrderCommand(
        CustomerId customerId,
        List<CreateOrderItem> items,
        Instant createdAt) {

}
