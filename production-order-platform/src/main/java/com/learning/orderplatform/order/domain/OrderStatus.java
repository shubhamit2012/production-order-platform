package com.learning.orderplatform.order.domain;

public record OrderStatus(String value) {
    public static final OrderStatus CREATED = new OrderStatus("CREATED");
}
