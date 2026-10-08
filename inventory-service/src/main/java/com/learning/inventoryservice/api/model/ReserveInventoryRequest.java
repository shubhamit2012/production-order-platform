package com.learning.inventoryservice.api.model;

import java.util.List;

public record ReserveInventoryRequest(List<ReserveInventoryItems> items) {
}
