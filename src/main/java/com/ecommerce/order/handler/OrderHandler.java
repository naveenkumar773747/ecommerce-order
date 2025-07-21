package com.ecommerce.order.handler;

import com.ecommerce.order.model.OrderRequest;
import com.ecommerce.order.service.OrderService;
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
    private OrderService orderService;

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
