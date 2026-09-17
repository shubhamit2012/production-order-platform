package com.learning.orderservice.application.port.in;

import com.learning.orderservice.application.model.CreateOrderCommand;
import com.learning.orderservice.domain.OrderId;

public interface CreateOrderUseCase {

    OrderId create(CreateOrderCommand command);

}
