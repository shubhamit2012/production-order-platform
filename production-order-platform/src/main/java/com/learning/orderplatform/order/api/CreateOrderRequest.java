package com.learning.orderplatform.order.api;

import com.learning.orderplatform.order.application.model.CreateOrderItem;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(UUID customerId,
                                 List<CreateOrderItemRequest> items) {

}
