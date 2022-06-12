package com.chat.server.infrastructure.rest;

import com.chat.server.domain.registration.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;

@Getter
class User {
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    @Id
    @Column(name = "username", unique = true)
    @JsonProperty("username")
    private final String username;
    @Column(name = "password", nullable = false)
    @JsonProperty("password")
    private final String password;
    UserDto dto(){
        return new UserDto(username, password);
    }
}
