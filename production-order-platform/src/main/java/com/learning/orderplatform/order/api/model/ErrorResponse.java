package com.learning.orderplatform.order.api.model;

public record ErrorResponse(
        String code,
        String message
) {
}
