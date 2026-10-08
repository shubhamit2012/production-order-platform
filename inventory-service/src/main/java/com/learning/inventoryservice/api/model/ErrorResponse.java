package com.learning.inventoryservice.api.model;

public record ErrorResponse(
        String code,
        String message
) {
}
