package com.learning.inventoryservice.application.exception;

public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(String message) {
        super(message);
    }
}