package com.learning.orderservice.api.model;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(UUID customerId,
                                 List<CreateOrderItemRequest> items) {

}
