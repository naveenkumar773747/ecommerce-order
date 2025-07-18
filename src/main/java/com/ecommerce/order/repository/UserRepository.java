package com.ecommerce.order.repository;

import com.ecommerce.shared.model.UserInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveMongoRepository<UserInfo, String> {
}