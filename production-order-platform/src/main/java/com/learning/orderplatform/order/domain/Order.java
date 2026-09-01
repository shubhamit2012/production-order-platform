package com.learning.orderplatform.order.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private final OrderId id;
    private final CustomerId customerId;
    private OrderStatus status;
    private final List<OrderItem> items;
    private final Instant createdAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Order(OrderId orderId, CustomerId customerId, List<OrderItem> items, Instant createdAt) {
        this.id = orderId;
        this.status = OrderStatus.CREATED;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.createdAt = createdAt;
    }

    public OrderId orderId() {
        return this.id;
    }

    public CustomerId customerId() {
        return this.customerId;
    }

    public OrderStatus orderStatus() {
        return this.status;
    }

    public List<OrderItem> items() {
        return this.items;
    }

    public Instant createdAt() {
        return this.createdAt;
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    public static Order create(OrderId orderId, CustomerId customerId, List<OrderItem> items, Instant createdAt) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items cannot be null or empty");
        }

        for (OrderItem item : items) {
            if (item.quantity() <= 0) {
                throw new IllegalArgumentException("item quantity cannot be zero");
            }
        }

        DomainEvent orderCreatedEvent = new OrderCreatedEvent(orderId, customerId, createdAt);
        Order order = new Order(orderId, customerId, items, createdAt);
        order.domainEvents.add(orderCreatedEvent);
        return order;
    }

}
