package com.learning.orderplatform.order.api.model;

import java.util.UUID;

public record CreateOrderItemRequest(
        UUID productId,
        int quantity) {

}
