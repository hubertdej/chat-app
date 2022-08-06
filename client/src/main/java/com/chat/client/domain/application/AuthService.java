package com.chat.client.domain.application;

import com.chat.client.domain.Account;

import java.util.concurrent.CompletableFuture;

public interface AuthService {
    CompletableFuture<Void> registerUserAsync(String username, String password);
    CompletableFuture<Account> loginUserAsync(String username, String password);
}
