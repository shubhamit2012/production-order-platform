package com.learning.orderplatform.order.api;

import com.learning.orderplatform.order.application.model.CreateOrderCommand;
import com.learning.orderplatform.order.application.model.CreateOrderItem;
import com.learning.orderplatform.order.domain.CustomerId;
import com.learning.orderplatform.order.domain.ProductId;

import java.time.Instant;
import java.util.List;

public class CreateOrderMapper {

    public CreateOrderCommand toCreateOrderCommand(CreateOrderRequest request) {
        List<CreateOrderItem> createOrderItems = request.items().stream()
                .map(item -> new CreateOrderItem(new ProductId(item.productId()), item.quantity()))
                .toList();
        return new CreateOrderCommand(new CustomerId(request.customerId()), createOrderItems, Instant.now());
    }
}
