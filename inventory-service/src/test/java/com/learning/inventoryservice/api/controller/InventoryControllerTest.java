package com.learning.inventoryservice.api.controller;

import com.learning.inventoryservice.api.mapper.ReserveInventoryMapper;
import com.learning.inventoryservice.api.model.ReserveInventoryItems;
import com.learning.inventoryservice.application.command.InventoryItemCommand;
import com.learning.inventoryservice.application.command.ReserveInventoryCommand;
import com.learning.inventoryservice.application.usecase.ReserveInventoryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryControllerTest {

    @Test
    void reserve_acceptsListPayload() {
        List<ReserveInventoryItems> items = List.of(
                new ReserveInventoryItems(UUID.fromString("00000000-0000-0000-0000-000000000001"), 2),
                new ReserveInventoryItems(UUID.fromString("00000000-0000-0000-0000-000000000002"), 3)
        );

        CapturingUseCase useCase = new CapturingUseCase();
        InventoryController controller = new InventoryController(useCase, new ReserveInventoryMapper());

        ResponseEntity<String> response = controller.reserve(items);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inventory reserved successfully", response.getBody());
        assertEquals(2, useCase.capturedCommand.inventoryItems().size());
        assertEquals(new InventoryItemCommand(new com.learning.inventoryservice.domain.ProductId(UUID.fromString("00000000-0000-0000-0000-000000000001")), 2),
                useCase.capturedCommand.inventoryItems().get(0));
        assertEquals(new InventoryItemCommand(new com.learning.inventoryservice.domain.ProductId(UUID.fromString("00000000-0000-0000-0000-000000000002")), 3),
                useCase.capturedCommand.inventoryItems().get(1));
    }

    private static final class CapturingUseCase implements ReserveInventoryUseCase {

        private ReserveInventoryCommand capturedCommand;

        @Override
        public void reserve(ReserveInventoryCommand command) {
            this.capturedCommand = command;
        }
    }
}
