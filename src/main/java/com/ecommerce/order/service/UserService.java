package com.ecommerce.order.service;

import com.ecommerce.order.producer.UserProducer;
import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.order.repository.UserRepository;
import com.ecommerce.shared.model.Cart;
import com.ecommerce.shared.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserService {


    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserProducer userProducer;

    public Mono<UserInfo> addUser(UserInfo user) {
        return Mono.justOrEmpty(user)
                .flatMap(userInfo ->
                        userProducer.sendMessage(userInfo.toString())
                                .doOnNext(user1 -> log.info("Published to user_topic : {}", user))
                                .flatMap(user1 -> {
                                    Cart cart = new Cart();
                                    cart.setId(UUID.randomUUID().toString().split("-")[0]);
                                    cart.setUserId(userInfo.getUserId());
                                    return cartRepository.save(cart)
                                            .flatMap(cart1 -> userRepository.save(user));
                                })
                                .doOnNext(user1 -> log.info("Saved to DB : {}", user))
                );
    }

    public Mono<UserInfo> getUserById(String userId) {
        return Mono.justOrEmpty(userId)
                .flatMap(userRepository::findById);
    }

    public Flux<UserInfo> getAllUsers() {
        return userRepository.findAll();
    }

}
