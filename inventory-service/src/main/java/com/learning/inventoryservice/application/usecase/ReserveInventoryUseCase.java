package com.learning.inventoryservice.application.usecase;

import com.learning.inventoryservice.application.command.ReserveInventoryCommand;

public interface ReserveInventoryUseCase {

    void reserve(ReserveInventoryCommand command);

}
