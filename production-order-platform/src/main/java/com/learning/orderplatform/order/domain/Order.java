package com.learning.orderplatform.order.domain;

import java.time.Instant;
import java.util.List;

public class Order {

    private final OrderId orderId;
    private final CustomerId customerId;
    private OrderStatus orderStatus;
    private final List<OrderItem> items;
    private final Instant createdAt;
    private List<DomainEvent> domainEvents;

    private Order(OrderId orderId, CustomerId customerId, List<OrderItem> items, Instant createdAt, List<DomainEvent> domainEvents) {
        this.orderId = orderId;
        this.orderStatus = OrderStatus.CREATED;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.createdAt = createdAt;
        this.domainEvents = List.copyOf(domainEvents);
    }

    public OrderId orderId() {
        return this.orderId;
    }

    public CustomerId customerId() {
        return this.customerId;
    }

    public OrderStatus orderStatus() {
        return this.orderStatus;
    }

    public List<OrderItem> items() {
        return this.items;
    }

    public Instant createdAt() {
        return this.createdAt;
    }

    public List<DomainEvent> domainEvents() {
        return this.domainEvents;
    }

    public static Order create(OrderId orderId, CustomerId customerId, List<OrderItem> items) {
        if (items == null || items.size() == 0) {
            throw new IllegalArgumentException("items cannot be null or empty");
        }

        for (OrderItem item : items) {
            if (item.quantity() == 0) {
                throw new IllegalArgumentException("item quantity cannot be zero");
            }
        }

        Instant createdAt = Instant.now();
        List<DomainEvent> orderCreatedEvent = List.of(new OrderCreatedEvent(orderId, customerId, createdAt));
        return new Order(orderId, customerId, items, createdAt, orderCreatedEvent);
    }

}
