package com.learning.orderplatform.order.infrastructure.persistence;

import com.learning.orderplatform.order.application.port.out.OrderRepository;
import com.learning.orderplatform.order.domain.Order;
import com.learning.orderplatform.order.infrastructure.persistence.entity.OrderEntity;
import com.learning.orderplatform.order.infrastructure.persistence.mapper.OrderEntityMapper;
import com.learning.orderplatform.order.infrastructure.persistence.spring.OrderEntityRepository;

public class JpaOrderRepository implements OrderRepository {

    private final OrderEntityMapper orderEntityMapper;
    private final OrderEntityRepository orderEntityRepository;

    public JpaOrderRepository(OrderEntityMapper orderEntityMapper, OrderEntityRepository orderEntityRepository) {
        this.orderEntityMapper = orderEntityMapper;
        this.orderEntityRepository = orderEntityRepository;
    }

    @Override
    public void save(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(order);
        orderEntityRepository.save(orderEntity);
    }

}
