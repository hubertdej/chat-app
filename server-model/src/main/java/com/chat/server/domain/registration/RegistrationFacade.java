package com.chat.server.domain.registration;

import com.chat.server.domain.ConversationsEngine;
import com.chat.server.domain.UsersEngine;
import com.chat.server.domain.registration.dto.UserDto;
import com.chat.server.domain.registration.dto.UsernameTakenException;

import java.util.List;
import java.util.Optional;

public class RegistrationFacade {
    private final CredentialsRepository credentialsRepository;
    private final UsersEngine engine;

    public RegistrationFacade(CredentialsRepository credentialsRepository, UsersEngine engine) {
        this.credentialsRepository = credentialsRepository;
        this.engine = engine;
        engine.readUsers((username, password) -> credentialsRepository.save(new User(username, password)));
    }

    public void register(UserDto userDto) throws UsernameTakenException {
        credentialsRepository.save(new UserCreator().create(userDto));
        engine.addUser(userDto.username(), userDto.password());
    }
    //TODO replace Optional with type containing error info
    public Optional<String> getCredentials(String username){
        return credentialsRepository.findById(username);
    }

    public List<String> listUsers(){
        return credentialsRepository.listUsers();
    }
}
