package com.ecommerce.order.service;

import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.shared.model.Cart;
import com.ecommerce.shared.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    @Autowired
    private CartRepository cartRepository;

    public Mono<Cart> addItem(String userId, CartItem item) {
        return cartRepository.findByUserId(userId)
                .flatMap(cart -> {
                    cart.addOrUpdateItem(item);
                    return cartRepository.save(cart)
                            .doOnNext(cart1 -> log.info("Item saved to cart with id : {} for userId : {} : {}", cart.getId(), userId, cart))
                            .doOnError(err -> log.info("Saving item to cart with id : {} failed for userId : {} : {}", cart.getId(), userId, item));
                });
    }

    public Mono<Cart> updateItem(String userId, String productId, CartItem item) {
        return cartRepository.findById(userId)
                .flatMap(cart -> {
                    cart.updateItem(productId, item.getQuantity());
                    return cartRepository.save(cart);
                });
    }

    public Mono<Cart> removeItem(String userId, String productId) {
        return cartRepository.findByUserId(userId)
                .flatMap(cart -> {
                    cart.removeItem(productId);
                    return cartRepository.save(cart);
                });
    }

    public Mono<Cart> getCart(String userId) {
        return cartRepository.findByUserId(userId);
    }
}