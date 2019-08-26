package com.example.projectwebflux.user.endpoint;

import com.example.projectwebflux.user.model.User;
import com.example.projectwebflux.user.service.UserService;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UserEndpointHandler {

    private final UserService userService;

    UserEndpointHandler(UserService userService) {
        this.userService = userService;
    }

    Mono<ServerResponse> getById(ServerRequest request) {
        return defaultReadResponse(this.userService.get(id(request)));
    }

    Mono<ServerResponse> all(ServerRequest request) {
        return defaultReadResponse(this.userService.all());
    }

    Mono<ServerResponse> deleteById(ServerRequest request) {
        return defaultReadResponse(this.userService.delete(id(request)));
    }

    Mono<ServerResponse> updateById(ServerRequest request) {
        Flux<User> id = request.bodyToFlux(User.class)
                .flatMap(p -> this.userService.update(id(request), p.getName(), p.getLastName(), p.getEmail()));
        return defaultReadResponse(id);
    }

    Mono<ServerResponse> create(ServerRequest request) {
        Flux<User> flux = request
                .bodyToFlux(User.class)
                .flatMap(user -> this.userService.create(user.getName(), user.getLastName(), user.getEmail()));
        return defaultWriteResponse(flux);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<User> users) {
        return Mono
                .from(users)
                .flatMap(user -> ServerResponse
                        .created(URI.create("/users/" + user.getId()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<User> users) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(users, User.class);
    }

    private static String id(ServerRequest r) {
        return r.pathVariable("id");
    }
}
