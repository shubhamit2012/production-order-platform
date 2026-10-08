package com.learning.inventoryservice.application.command;

import com.learning.inventoryservice.domain.ProductId;

public record InventoryItemCommand(
        ProductId productId,
        int quantity) {

}
