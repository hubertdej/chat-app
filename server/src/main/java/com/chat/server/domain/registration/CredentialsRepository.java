package com.chat.server.domain.registration;

import com.chat.server.domain.registration.dto.UsernameTakenException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

interface CredentialsRepository {
    void save(User user);
    Optional<String> findById(String username);
    List<String> listUsers();
}

class InMemoryCredentialsRepository implements CredentialsRepository {
    Map<String, String> storage = new HashMap<>();

    @Override
    public void save(User user) {
        if(storage.containsKey(user.username()))
            throw new UsernameTakenException();
        storage.put(user.username(), user.password());
    }

    @Override
    public Optional<String> findById(String username) {
        return Optional.ofNullable(storage.get(username));
    }

    @Override
    public List<String> listUsers() {
        return storage.keySet().stream().toList();
    }
}




