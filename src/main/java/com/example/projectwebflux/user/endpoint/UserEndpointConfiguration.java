package com.example.projectwebflux.user.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserEndpointConfiguration {

    @Bean
    public RouterFunction<ServerResponse> routes(UserEndpointHandler handler) {
        return route(GET("/users"), handler::all)
                .andRoute(GET("/users/{id}"), handler::getById)
                .andRoute(DELETE("/users/{id}"), handler::deleteById)
                .andRoute(POST("/users"), handler::create)
                .andRoute(PUT("/users/{id}"), handler::updateById);
    }
}
