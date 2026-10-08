package com.learning.inventoryservice.application.port.out;

import com.learning.inventoryservice.domain.Inventory;

public interface InventoryRepository {

    void save(Inventory inventory);

}
