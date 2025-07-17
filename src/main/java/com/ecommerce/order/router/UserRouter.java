package com.ecommerce.order.router;

import com.ecommerce.order.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRoute(UserHandler handler) {
        return RouterFunctions.route()
                .POST("/users/add", handler::addUser)
                .GET("/users", handler::getAllUsers)
                .GET("/users/{userId}", handler::getUserById)
                .build();
    }
}
