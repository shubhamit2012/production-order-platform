package com.learning.orderplatform.order.infrastructure.inventory.model;

import java.util.UUID;

public record InventoryRequest(UUID productId, int quantity) {
}
