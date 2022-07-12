package com.chat.client.domain;

import java.util.concurrent.CompletableFuture;

public interface AccountRepository {
    CompletableFuture<Void> registerUserAsync(String username, String password);
    CompletableFuture<Account> loginUserAsync(String username, String password);
}
