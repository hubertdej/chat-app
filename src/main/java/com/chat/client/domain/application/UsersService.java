package com.chat.client.domain.application;

import com.chat.client.domain.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UsersService {
    CompletableFuture<List<User>> getUsersAsync();
}
