package com.learning.orderplatform.order.application.port.in;

import com.learning.orderplatform.order.domain.exceptions.InsufficientInventoryException;
import com.learning.orderplatform.order.application.model.CreateOrderCommand;
import com.learning.orderplatform.order.domain.OrderId;

public interface CreateOrderUseCase {

    OrderId create(CreateOrderCommand command) throws InsufficientInventoryException;

}
