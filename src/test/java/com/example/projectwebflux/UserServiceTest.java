package com.example.projectwebflux;

import com.example.projectwebflux.user.model.User;
import com.example.projectwebflux.user.repository.UserRepository;
import com.example.projectwebflux.user.service.UserService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;
import java.util.function.Predicate;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Log4j2
@DataMongoTest
@Import(UserService.class)
public class UserServiceTest {

	private final UserService userService;
	private final UserRepository userRepository;

	public UserServiceTest(@Autowired UserService userService,
						   @Autowired UserRepository userRepository) {
		this.userService = userService;
		this.userRepository = userRepository;
	}

	@Test //TODO transactional
	@Disabled
	void getAll() {
		Flux<User> saved = userRepository.saveAll(Flux
				.just(
						User.builder().id(UUID.randomUUID().toString()).name("Sherlock").lastName("Holmes").email("sherlock.holmes@email.com").build(),
						User.builder().id(UUID.randomUUID().toString()).name("John").lastName("Watson").email("john.watson@email.com").build()));

		Flux<User> composite = userService.all()
			.thenMany(saved);

		Predicate<User> match = user -> saved.any(saveItem -> saveItem.equals(user)).block();

		StepVerifier
				.create(composite)
				.expectNextMatches(match)
				.expectNextMatches(match)
				.verifyComplete();
	}

	@Test
	void save() {
		Mono<User> user = this.userService.create("Hercules", "Poirot", "hercules.poirot@email.com");

		StepVerifier
				.create(user)
				.expectNextMatches(savedUser -> savedUser.getId() != null)
				.verifyComplete();
	}

	@Test
	void delete() {
		Mono<User> deletedUser = this.userService
				.create("Auguste", "Dupin", "auguste.dupin@email.com")
				.flatMap(savedUser -> this.userService.delete(savedUser.getId()));

		StepVerifier
				.create(deletedUser)
				.expectNextMatches(user -> user.getEmail().equalsIgnoreCase("auguste.dupin@email.com"))
				.verifyComplete();
	}

	@Test
	void update() {
		Mono<User> savedUser = this.userService
				.create("harry", "hole","harry.hole@email.com")
				.flatMap(user -> this.userService.update(user.getId(), user.getName(), user.getLastName(), "harry.hole.new@email.com"));

		StepVerifier
				.create(savedUser)
				.expectNextMatches(user -> user.getEmail().equalsIgnoreCase("harry.hole.new@email.com"))
				.verifyComplete();
	}

	@Test
	void getById() {
		Mono<User> savedUser = this.userService
				.create("jules", "maigret", "jules.maigret@email.com")
				.flatMap(user -> this.userService.get(user.getId()));

		StepVerifier
				.create(savedUser)
				.expectNextMatches(user -> user.getId() != null)
				.verifyComplete();
	}
}
