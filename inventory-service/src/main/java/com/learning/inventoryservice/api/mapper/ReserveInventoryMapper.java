package com.learning.inventoryservice.api.mapper;

import com.learning.inventoryservice.api.model.ReserveInventoryRequest;
import com.learning.inventoryservice.application.command.InventoryItemCommand;
import com.learning.inventoryservice.application.command.ReserveInventoryCommand;
import com.learning.inventoryservice.domain.ProductId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReserveInventoryMapper {

    public ReserveInventoryCommand toCommand(ReserveInventoryRequest reserveInventoryRequest) {
        List<InventoryItemCommand> inventoryItemCommands = reserveInventoryRequest.items()
                .stream()
                .map(item -> new InventoryItemCommand(new ProductId(item.productId()), item.quantity()))
                .toList();
        return new ReserveInventoryCommand(inventoryItemCommands);
    }
}
