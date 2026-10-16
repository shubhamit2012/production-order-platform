package com.learning.inventoryservice.application.command;

import java.util.List;

public record ReserveInventoryCommand(
                List<InventoryItemCommand> inventoryItems) {
}
