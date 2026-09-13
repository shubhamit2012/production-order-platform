package com.learning.orderplatform.order.api.model;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(UUID customerId,
                                 List<CreateOrderItemRequest> items) {

}
