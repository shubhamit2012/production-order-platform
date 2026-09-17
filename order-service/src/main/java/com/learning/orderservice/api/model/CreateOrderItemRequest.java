package com.learning.orderservice.api.model;

import java.util.UUID;

public record CreateOrderItemRequest(
        UUID productId,
        int quantity) {

}
