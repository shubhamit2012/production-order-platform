package com.learning.orderplatform.order.application.model;

import com.learning.orderplatform.order.domain.ProductId;

public record InventoryReservationRequest(
        ProductId productId,
        int quantity) {
}
