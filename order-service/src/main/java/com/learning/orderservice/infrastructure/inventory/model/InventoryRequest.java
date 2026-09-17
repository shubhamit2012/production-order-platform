package com.learning.orderservice.infrastructure.inventory.model;

import java.util.UUID;

public record InventoryRequest(UUID productId, int quantity) {
}
