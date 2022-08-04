package com.chat.server.domain.registration;

import com.chat.server.domain.registration.dto.UserDto;
import com.chat.server.domain.registration.dto.UsernameTakenException;

import java.util.List;
import java.util.Optional;


public class RegistrationFacade {
    private final CredentialsRepository credentialsRepository;

    public RegistrationFacade(CredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }

    public void register(UserDto userDto) throws UsernameTakenException {
        credentialsRepository.save(new UserCreator().create(userDto));
    }
    //TODO replace Optional with type containing error info
    public Optional<String> getCredentials(String username){
        return credentialsRepository.findById(username);
    }

    public List<String> listUsers(){
        return credentialsRepository.listUsers();
    }
}
