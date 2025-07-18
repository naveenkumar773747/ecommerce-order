package com.ecommerce.order.handler;

import com.ecommerce.order.model.OrderRequest;
import com.ecommerce.order.producer.OrderProducer;
import com.ecommerce.order.service.CartService;
import com.ecommerce.order.service.OrderService;
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
public class OrderHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderHandler.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderProducer orderProducer;

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

    /**
     * This method takes ServerRequest that contains OrderRequest in the body and userId in the path variable to
     * handle in placing a new order.
     *
     * @param request : ServerRequest containing order and userId
     * @return ServerResponse mono : with order confirmation
     */
    public Mono<ServerResponse> placeOrder(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return request.bodyToMono(OrderRequest.class)
                .flatMap(order -> orderService.placeOrder(userId, order))
                .doOnNext(order -> log.info("Order placed with id : {} for userId : {} = {}", order.getOrderId(), userId, order))
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved));
    }

    /**
     * This method takes ServerRequest that contains userId in the path variable to retrieve all the orders of a user.
     *
     * @param request : ServerRequest containing userId
     * @return ServerResponse mono : with all the orders of a user
     */
    public Mono<ServerResponse> getUserOrders(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return orderService.getOrdersByUser(userId)
                .collectList()
                .doOnNext(orders -> log.info("All orders for userId : {} : {}", userId, orders))
                .flatMap(list -> ServerResponse.ok().bodyValue(list));
    }

    /**
     * This method takes ServerRequest that contains orderId in the path variable to retrieve the order details.
     *
     * @param request : ServerRequest containing orderId
     * @return ServerResponse mono : with the order details
     */
    public Mono<ServerResponse> getOrderById(ServerRequest request) {
        String orderId = request.pathVariable("orderId");
        return orderService.getOrderById(orderId)
                .doOnNext(order -> log.info("Retrieved order with id : {} : {}", orderId, order))
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
