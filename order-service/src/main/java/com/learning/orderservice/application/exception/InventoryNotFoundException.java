package com.learning.orderservice.application.exception;

public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}