package com.learning.inventoryservice.application.port.out;

import com.learning.inventoryservice.domain.Inventory;
import com.learning.inventoryservice.domain.ProductId;

public interface InventoryRepository {

    void save(Inventory inventory);

    Inventory findByProductId(ProductId productId);

}
