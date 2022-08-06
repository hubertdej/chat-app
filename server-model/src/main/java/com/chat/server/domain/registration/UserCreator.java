package com.chat.server.domain.registration;

import com.chat.server.domain.registration.dto.UserDto;

public class UserCreator {
    User create(UserDto userDto){
        return new User(userDto.username(), userDto.password());
    }
}
