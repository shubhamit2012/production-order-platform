package com.learning.orderservice.api.model;

public record ErrorResponse(
        String code,
        String message
) {
}
