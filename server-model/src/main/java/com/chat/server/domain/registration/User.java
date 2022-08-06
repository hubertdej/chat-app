package com.chat.server.domain.registration;

import com.chat.server.domain.registration.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

record User(String username, String password) {
    UserDto dto() {
        return new UserDto(username, password);
    }
}
