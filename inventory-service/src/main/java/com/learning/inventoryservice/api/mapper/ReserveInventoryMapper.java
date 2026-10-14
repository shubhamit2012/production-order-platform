package com.learning.inventoryservice.api.mapper;

import com.learning.inventoryservice.api.model.ReserveInventoryItems;
import com.learning.inventoryservice.api.model.ReserveInventoryRequest;
import com.learning.inventoryservice.application.command.InventoryItemCommand;
import com.learning.inventoryservice.application.command.ReserveInventoryCommand;
import com.learning.inventoryservice.domain.ProductId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReserveInventoryMapper {

    public ReserveInventoryCommand toCommand(ReserveInventoryRequest reserveInventoryRequest) {
        return toCommandFromItems(reserveInventoryRequest.items());
    }

    public ReserveInventoryCommand toCommandFromItems(List<ReserveInventoryItems> reserveInventoryItems) {
        List<InventoryItemCommand> inventoryItemCommands = reserveInventoryItems.stream()
                .map(item -> new InventoryItemCommand(new ProductId(item.productId()), item.quantity()))
                .toList();
        return new ReserveInventoryCommand(inventoryItemCommands);
    }
}
