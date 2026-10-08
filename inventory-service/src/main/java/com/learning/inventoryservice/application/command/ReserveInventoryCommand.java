package com.learning.inventoryservice.application.command;

import com.learning.inventoryservice.domain.ProductId;

import java.util.List;

public record ReserveInventoryCommand(
        List<InventoryItemCommand> inventoryItems
) {
}
