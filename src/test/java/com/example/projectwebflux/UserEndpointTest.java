package com.example.projectwebflux;

import com.example.projectwebflux.user.model.User;
import com.example.projectwebflux.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserEndpointTest {

    private WebTestClient client;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    RouterFunction<ServerResponse> routes;

    @BeforeAll
    void init() {
        this.client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void getAll() {
        Mockito
                .when(this.userRepository.findAll())
                .thenReturn(Flux.just(
                        User.builder().id(UUID.randomUUID().toString()).name("Sherlock").lastName("Holmes").email("sherlock.holmes@email.com").build(),
                        User.builder().id(UUID.randomUUID().toString()).name("John").lastName("Watson").email("john.watson@email.com").build()));

        this.client
                .get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.[0].name").isEqualTo("Sherlock")
                .jsonPath("$.[0].lastName").isEqualTo("Holmes")
                .jsonPath("$.[0].email").isEqualTo("sherlock.holmes@email.com")
                .jsonPath("$.[1].name").isEqualTo("John")
                .jsonPath("$.[1].lastName").isEqualTo("Watson")
                .jsonPath("$.[1].email").isEqualTo("john.watson@email.com");
    }

    @Test
    void save() {
        User user = User.builder().id(UUID.randomUUID().toString()).name("Harry").lastName("Hole").email("harry.hole@email.com").build();

        Mockito
                .when(this.userRepository.save(Mockito.any(User.class)))
                .thenReturn(Mono.just(user));

        this.client
                .post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(user), User.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Test
    void delete() {
        User user = User.builder().id(UUID.randomUUID().toString()).name("Jack").lastName("Reacher").email("jack.reacher@email.com").build();

        Mockito
                .when(this.userRepository.findById(user.getId()))
                .thenReturn(Mono.just(user));

        Mockito
                .when(this.userRepository.deleteById(user.getId()))
                .thenReturn(Mono.empty());

        this.client
                .delete()
                .uri("/users/" + user.getId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void update() {
        User user = User.builder().id(UUID.randomUUID().toString()).name("Kurt").lastName("Wallander").email("kurt.wallander@email.com").build();

        Mockito
                .when(this.userRepository.findById(user.getId()))
                .thenReturn(Mono.just(user));

        Mockito
                .when(this.userRepository.save(user))
                .thenReturn(Mono.just(user));

        this.client
                .put()
                .uri("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(user), User.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getById() {
        User user = User.builder().id(UUID.randomUUID().toString()).name("Jane").lastName("Marple").email("jane.marple@email.com").build();

        Mockito
                .when(this.userRepository.findById(user.getId()))
                .thenReturn(Mono.just(user));

        this.client
                .get()
                .uri("/users/" + user.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.name").isEqualTo(user.getName())
                .jsonPath("$.lastName").isEqualTo(user.getLastName())
                .jsonPath("$.email").isEqualTo(user.getEmail());
    }
}
