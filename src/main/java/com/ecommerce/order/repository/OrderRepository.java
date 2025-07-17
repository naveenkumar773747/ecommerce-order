package com.ecommerce.order.repository;

import com.ecommerce.shared.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    Flux<Order> findOrdersByUserId(String userId);
}