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

    /**
     * This method takes ServerRequest that contains UserInfo in the body to create a new user
     *
     * @param request : ServerRequest containing UserInfo
     * @return ServerResponse mono : with the UserInfo details
     */
    public Mono<ServerResponse> addUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserInfo.class)
                .flatMap(item -> userService.addUser(item))
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }

    /**
     * This method takes ServerRequest to retrieve all users
     *
     * @param request : ServerRequest
     * @return ServerResponse mono : with all the UserInfo details
     */
    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok().body(userService.getAllUsers(), UserInfo.class);
    }

    /**
     * This method takes ServerRequest that contains userId in the path variable to retrieve user
     *
     * @param request : ServerRequest containing userId
     * @return ServerResponse mono : with the UserInfo having queried userId
     */
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return userService.getUserById(userId)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
