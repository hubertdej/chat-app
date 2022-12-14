package com.chat.server.domain.registration;

import com.chat.server.domain.registration.dto.UserDto;
import com.chat.server.domain.registration.dto.UsernameTakenException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegistrationFacade {
    private final CredentialsRepository credentialsRepository;
    private final List<RegistrationObserver> observers = new ArrayList<>();
    public RegistrationFacade(CredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }
    public void addObserver(RegistrationObserver observer) {
        observers.add(observer);
    }
    public interface RegistrationObserver {
        void notifyRegistered(UserDto user);
    }

    public void register(UserDto userDto) throws UsernameTakenException {
        credentialsRepository.save(new UserCreator().create(userDto));
        for (RegistrationObserver observer : observers) observer.notifyRegistered(userDto);
    }
    //TODO replace Optional with type containing error info
    public Optional<String> getCredentials(String username){
        return credentialsRepository.findById(username);
    }

    public List<String> listUsers(){
        return credentialsRepository.listUsers();
    }

}
