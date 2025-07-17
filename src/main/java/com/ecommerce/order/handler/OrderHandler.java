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
//@Slf4j
public class OrderHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderHandler.class);
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderProducer orderProducer;

    public Mono<ServerResponse> addItemToCart(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return request.bodyToMono(CartItem.class)
                .flatMap(item -> cartService.addItem(userId, item))
                .doOnNext(cart -> log.info("Item added to cart with id : {} for userId : {}", cart.getId(), userId))
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart));
    }

    public Mono<ServerResponse> updateCartItem(ServerRequest request) {
        String userId = request.pathVariable("userId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(CartItem.class)
                .flatMap(item -> cartService.updateItem(userId, productId, item))
                .doOnNext(cart -> log.info("Item updated in cart with id : {} for userId : {}", cart.getId(), userId))
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart));
    }

    public Mono<ServerResponse> removeCartItem(ServerRequest request) {
        String userId = request.pathVariable("userId");
        String productId = request.pathVariable("productId");
        return cartService.removeItem(userId, productId)
                .doOnNext(cart -> log.info("Item removed from cart with id : {} for userId : {}", cart.getId(), userId))
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart));
    }

    public Mono<ServerResponse> getCart(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return cartService.getCart(userId)
                .doOnNext(cart -> log.info("Items in cart with id : {} for userId : {} = {}", cart.getId(), userId, cart))
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart));
    }

    public Mono<ServerResponse> placeOrder(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return request.bodyToMono(OrderRequest.class)
                .flatMap(order -> orderService.placeOrder(userId, order))
                .doOnNext(order -> log.info("Order placed with id : {} for userId : {} = {}", order.getOrderId(), userId, order))
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved));
    }

    public Mono<ServerResponse> getUserOrders(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return orderService.getOrdersByUser(userId)
                .collectList()
                .doOnNext(orders -> log.info("All orders for userId : {} : {}", userId, orders))
                .flatMap(list -> ServerResponse.ok().bodyValue(list));
    }

    public Mono<ServerResponse> getOrderById(ServerRequest request) {
        String orderId = request.pathVariable("orderId");
        return orderService.getOrderById(orderId)
                .doOnNext(order -> log.info("Retrieved order with id : {} : {}", orderId, order))
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
