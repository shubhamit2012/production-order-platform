package com.learning.orderservice.application.exception;

public class InventoryServiceException extends RuntimeException {
    public InventoryServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
