package com.example.projectwebflux.user.event;

import com.example.projectwebflux.user.model.User;

import org.springframework.context.ApplicationEvent;

public class UserCreatedEvent extends ApplicationEvent {

    public UserCreatedEvent(User user) {
        super(user);
    }
}
