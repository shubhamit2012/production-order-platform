package com.learning.orderplatform.order.api;

import java.util.UUID;

public record CreateOrderItemRequest(
        UUID productId,
        int quantity) {

}
