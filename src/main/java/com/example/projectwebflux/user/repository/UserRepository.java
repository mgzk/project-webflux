package com.example.projectwebflux.user.repository;

import com.example.projectwebflux.user.model.User;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
