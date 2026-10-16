package com.learning.inventoryservice.api.controller;

import com.learning.inventoryservice.api.mapper.ReserveInventoryMapper;
import com.learning.inventoryservice.api.model.ReserveInventoryItems;
import com.learning.inventoryservice.application.command.ReserveInventoryCommand;
import com.learning.inventoryservice.application.usecase.ReserveInventoryUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory/reservations")
public class InventoryController {

    private final ReserveInventoryUseCase reservationUseCase;
    private final ReserveInventoryMapper reserveInventoryMapper;

    public InventoryController(ReserveInventoryUseCase reservationUseCase,
            ReserveInventoryMapper reserveInventoryMapper) {
        this.reservationUseCase = reservationUseCase;
        this.reserveInventoryMapper = reserveInventoryMapper;
    }

    @PostMapping
    public ResponseEntity<String> reserve(@RequestBody List<ReserveInventoryItems> reserveInventoryItems) {
        ReserveInventoryCommand reserveInventoryCommand = reserveInventoryMapper
                .toCommandFromItems(reserveInventoryItems);
        reservationUseCase.reserve(reserveInventoryCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body("Inventory reserved successfully");
    }
}
