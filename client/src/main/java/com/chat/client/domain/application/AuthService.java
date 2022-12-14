package com.chat.client.domain.application;

import com.chat.client.domain.Credentials;

import java.util.concurrent.CompletableFuture;

public interface AuthService {
    CompletableFuture<Void> registerUserAsync(String username, String password);
    CompletableFuture<Credentials> loginUserAsync(String username, String password);
}
