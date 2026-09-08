package com.learning.orderplatform.order.infrastructure.persistence.adapter;

import com.learning.orderplatform.order.application.port.out.OrderRepository;
import com.learning.orderplatform.order.domain.Order;
import com.learning.orderplatform.order.infrastructure.persistence.jpa.entity.OrderEntity;
import com.learning.orderplatform.order.infrastructure.persistence.jpa.repository.OrderEntityJPARepository;
import com.learning.orderplatform.order.infrastructure.persistence.mapper.OrderEntityMapper;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderEntityMapper orderEntityMapper;
    private final OrderEntityJPARepository orderEntityRepository;

    public OrderRepositoryAdapter(OrderEntityMapper orderEntityMapper, OrderEntityJPARepository orderEntityRepository) {
        this.orderEntityMapper = orderEntityMapper;
        this.orderEntityRepository = orderEntityRepository;
    }

    @Override
    public void save(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(order);
        orderEntityRepository.save(orderEntity);
    }

}
