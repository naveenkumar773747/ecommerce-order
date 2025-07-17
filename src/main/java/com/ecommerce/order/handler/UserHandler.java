package com.ecommerce.order.handler;

import com.ecommerce.order.service.UserService;
import com.ecommerce.shared.model.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    @Autowired
    private UserService userService;

    public Mono<ServerResponse> addUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserInfo.class)
                .flatMap(item -> userService.addUser(item))
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok().body(userService.getAllUsers(), UserInfo.class);
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return userService.getUserById(userId)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
