package com.chat.server.infrastructure.rest;

import com.chat.server.registration.dto.UserDto;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;

class User {
    @Id
    @Column(name = "username", unique = true)
    String username;
    @Column(name = "password", nullable = false)
    String password;
    UserDto dto(){
        return new UserDto(username, password);
    }
}
