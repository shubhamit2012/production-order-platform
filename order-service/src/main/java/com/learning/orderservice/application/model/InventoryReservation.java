package com.learning.orderservice.application.model;

import com.learning.orderservice.domain.ProductId;

public record InventoryReservation(
        ProductId productId,
        int quantity) {
}
