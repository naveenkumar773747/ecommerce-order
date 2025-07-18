package com.ecommerce.order.repository;

import com.ecommerce.shared.model.Cart;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CartRepository extends ReactiveMongoRepository<Cart, String> {
    Mono<Cart> findByUserId(String userId);
}