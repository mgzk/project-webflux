package com.example.projectwebflux.data;

import com.example.projectwebflux.user.model.User;
import com.example.projectwebflux.user.repository.UserRepository;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@Component
@Profile("demo")
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        userRepository
                .deleteAll()
                .thenMany(
                        Flux
                                .just(User.builder().id("1").name("james").lastName("smith").email("james.smith@email.com").build(),
                                        User.builder().id("2").name("john").lastName("jones").email("john.jones@email.com").build(),
                                        User.builder().id("3").name("david").lastName("brown").email("david.brown@email.com").build(),
                                        User.builder().id("4").name("richard").lastName("williams").email("richard.williams@email.com").build())
                                .flatMap(userRepository::save)
                )
                .thenMany(userRepository.findAll())
                .subscribe(user -> log.info("User: " + user.getName() + " " + user.getLastName()));
    }
}
