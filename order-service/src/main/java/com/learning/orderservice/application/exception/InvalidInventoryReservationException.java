package com.learning.orderservice.application.exception;

public class InvalidInventoryReservationException extends RuntimeException {

    public InvalidInventoryReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}
