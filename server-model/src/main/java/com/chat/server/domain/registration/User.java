package com.chat.server.domain.registration;

import com.chat.server.domain.registration.dto.UserDto;

record User(String username, String password) {
    UserDto dto() {
        return new UserDto(username, password);
    }
}
