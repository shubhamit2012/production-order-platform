package com.learning.orderservice.api.mapper;

import com.learning.orderservice.api.model.CreateOrderRequest;
import com.learning.orderservice.application.model.CreateOrderCommand;
import com.learning.orderservice.application.model.CreateOrderItem;
import com.learning.orderservice.domain.CustomerId;
import com.learning.orderservice.domain.ProductId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class CreateOrderMapper {

    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        List<CreateOrderItem> createOrderItems = request.items().stream()
                .map(item -> new CreateOrderItem(new ProductId(item.productId()), item.quantity()))
                .toList();
        return new CreateOrderCommand(new CustomerId(request.customerId()), createOrderItems, Instant.now());
    }
}
