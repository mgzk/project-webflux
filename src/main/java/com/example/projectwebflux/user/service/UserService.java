package com.example.projectwebflux.user.service;

import com.example.projectwebflux.user.event.UserCreatedEvent;
import com.example.projectwebflux.user.model.User;
import com.example.projectwebflux.user.repository.UserRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class UserService {

    private final ApplicationEventPublisher publisher;
    private final UserRepository userRepository;

    UserService(ApplicationEventPublisher publisher, UserRepository userRepository) {
        this.publisher = publisher;
        this.userRepository = userRepository;
    }

    public Flux<User> all() {
        return this.userRepository.findAll();
    }

    public Mono<User> get(String id) {
        return this.userRepository.findById(id);
    }

    public Mono<User> update(String id, String name, String lastName, String email) {
        return this.userRepository
                .findById(id)
                .map(user -> new User (user.getId(), name, lastName, email))
                .flatMap(this.userRepository::save);
    }

    public Mono<User> delete(String id) {
        return this.userRepository
                .findById(id)
                .flatMap(user -> this.userRepository
                        .deleteById(user.getId())
                        .thenReturn(user));
    }

    public Mono<User> create(String name, String lastName, String email) {
        return this.userRepository
                .save(new User(null, name, lastName, email))
                .doOnSuccess(user -> this.publisher.publishEvent(new UserCreatedEvent(user)));
    }
}
