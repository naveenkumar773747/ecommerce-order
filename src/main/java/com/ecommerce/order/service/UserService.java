package com.ecommerce.order.service;

import com.ecommerce.order.producer.UserProducer;
import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.order.repository.UserRepository;
import com.ecommerce.order.util.JsonUtil;
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

    @Autowired
    private JsonUtil jsonUtil;

    /**
     * This method takes UserInfo to create a new user
     *
     * @param user : UserInfo details
     * @return UserInfo mono : with the UserInfo details
     */
    public Mono<UserInfo> addUser(UserInfo user) {
        return Mono.justOrEmpty(user)
                .flatMap(userInfo ->
                        jsonUtil.toJson(userInfo)
                                .flatMap(userJson ->
                                        userProducer.sendMessage(userJson)
                                                .doOnNext(user1 -> log.info("Published to user_topic : {}", userJson))
                                                .flatMap(user1 -> {
                                                    Cart cart = new Cart();
                                                    cart.setId(UUID.randomUUID().toString().split("-")[0]);
                                                    cart.setUserId(userInfo.getUserId());
                                                    return cartRepository.save(cart)
                                                            .flatMap(cart1 -> userRepository.save(user));
                                                })
                                                .doOnNext(user1 -> log.info("Saved to DB : {}", user))
                                ));
    }

    /**
     * This method takes userId to retrieve a user
     *
     * @param userId : userId of a user
     * @return UserInfo mono : with the UserInfo details
     */
    public Mono<UserInfo> getUserById(String userId) {
        return Mono.justOrEmpty(userId)
                .flatMap(userRepository::findById);
    }

    /**
     * This method doesn't take any parameters and retrieve all the users in DB
     *
     * @return UserInfo flux : with all the UserInfo details
     */
    public Flux<UserInfo> getAllUsers() {
        return userRepository.findAll();
    }

}
