package com.chat.server.domain.registration;

import com.chat.server.domain.registration.dto.UsernameTakenException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CredentialsRepository {
    void save(User user);
    Optional<String> findById(String username);
    List<String> listUsers();
}




