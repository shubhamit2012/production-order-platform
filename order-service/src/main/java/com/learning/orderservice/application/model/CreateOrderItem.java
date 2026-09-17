package com.learning.orderservice.application.model;

import com.learning.orderservice.domain.ProductId;

public record CreateOrderItem(
        ProductId productId,
        int quantity) {
}
