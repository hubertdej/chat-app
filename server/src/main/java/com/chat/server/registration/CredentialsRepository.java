package com.chat.server.registration;

import com.chat.server.registration.dto.UsernameTakenException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

interface CredentialsRepository {
    void saveCredentials(String username, String password);
    Optional<String> findById(String username);
}

class InMemoryCredentialsRepository implements CredentialsRepository {
    Map<String, String> storage = new HashMap<>();

    @Override
    public void saveCredentials(String username, String password) {
        if(storage.containsKey(username))
            throw new UsernameTakenException();
        storage.put(username, password);
    }

    @Override
    public Optional<String> findById(String username) {
        return Optional.ofNullable(storage.get(username));
    }
}




