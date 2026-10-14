package com.learning.inventoryservice.application.exception;

public class InvalidInventoryReservationException extends RuntimeException {

    public InvalidInventoryReservationException(String message) {
        super(message);
    }
}
