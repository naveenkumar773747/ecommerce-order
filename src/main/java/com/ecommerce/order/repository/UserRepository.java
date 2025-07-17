package com.ecommerce.order.repository;

import com.ecommerce.shared.model.UserInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<UserInfo, String> {
}