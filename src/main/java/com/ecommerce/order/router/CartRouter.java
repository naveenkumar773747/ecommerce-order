package com.ecommerce.order.router;

import com.ecommerce.order.handler.CartHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CartRouter {

    @Bean
    public RouterFunction<ServerResponse> cartRoute(CartHandler handler) {
        return RouterFunctions.route()
                .POST("/cart/{userId}/items", handler::addItemToCart)
                .PUT("/cart/{userId}/items/{productId}", handler::updateCartItem)
                .DELETE("/cart/{userId}/items/{productId}", handler::removeCartItem)
                .GET("/cart/{userId}", handler::getCart)
                .build();
    }
}