package com.learning.inventoryservice.api.model;

import java.util.UUID;

public record ReserveInventoryItems(UUID productId,
                                    int quantity) {
}
