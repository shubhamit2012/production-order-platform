package com.learning.orderplatform.order.application.model;

import com.learning.orderplatform.order.domain.ProductId;

public record CreateOrderItem(
        ProductId productId,
        int quantity) {
}
