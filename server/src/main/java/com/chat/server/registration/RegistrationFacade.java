package com.chat.server.registration;

import com.chat.server.registration.dto.UserDto;
import com.chat.server.registration.dto.UsernameTakenException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationFacade {
    CredentialsRepository credentialsRepository;
    public void register(UserDto userDto) throws UsernameTakenException {
        credentialsRepository.saveCredentials(userDto.username(), userDto.password());
    }
    //TODO replace Optional with type containing error info
    public Optional<String> getCredentials(String username){
        return credentialsRepository.findById(username);
    }
}
