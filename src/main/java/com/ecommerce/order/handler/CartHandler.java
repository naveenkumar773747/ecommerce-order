package com.ecommerce.order.handler;

import com.ecommerce.order.service.CartService;
import com.ecommerce.shared.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CartHandler {

    private static final Logger log = LoggerFactory.getLogger(CartHandler.class);

    @Autowired
    private CartService cartService;

    /**
     * This method takes ServerRequest that contains CartItem in the request body and userId in the path variable to add
     * the item into cart.
     *
     * @param request : ServerRequest containing CartItem and userId
     * @return ServerResponse mono : with the cart details
     */
    public Mono<ServerResponse> addItemToCart(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return request.bodyToMono(CartItem.class)
                .flatMap(item -> cartService.addItem(userId, item))
                .doOnNext(cart -> log.info("Item added to cart with id : {} for userId : {}", cart.getId(), userId))
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart));
    }

    /**
     * This method takes ServerRequest that contains CartItem in the body and userId, productId in the path variables to
     * update the item details in cart.
     *
     * @param request : ServerRequest containing CartItem, userId and productId
     * @return ServerResponse mono : with the updated cart details
     */
    public Mono<ServerResponse> updateCartItem(ServerRequest request) {
        String userId = request.pathVariable("userId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(CartItem.class)
                .flatMap(item -> cartService.updateItem(userId, productId, item))
                .doOnNext(cart -> log.info("Item updated in cart with id : {} for userId : {}", cart.getId(), userId))
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart));
    }

    /**
     * This method takes ServerRequest that contains userId and productId in the path variables to
     * remove a specific item in cart.
     *
     * @param request : ServerRequest containing CartItem, userId and productId
     * @return ServerResponse mono : with the updated cart details
     */
    public Mono<ServerResponse> removeCartItem(ServerRequest request) {
        String userId = request.pathVariable("userId");
        String productId = request.pathVariable("productId");
        return cartService.removeItem(userId, productId)
                .doOnNext(cart -> log.info("Item removed from cart with id : {} for userId : {}", cart.getId(), userId))
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart));
    }

    /**
     * This method takes ServerRequest that contains userId in the path variables to retrieve all the items in cart.
     *
     * @param request : ServerRequest containing userId
     * @return ServerResponse mono : with the updated cart details
     */
    public Mono<ServerResponse> getCart(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return cartService.getCart(userId)
                .doOnNext(cart -> log.info("Items in cart with id : {} for userId : {} = {}", cart.getId(), userId, cart))
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart));
    }
}
