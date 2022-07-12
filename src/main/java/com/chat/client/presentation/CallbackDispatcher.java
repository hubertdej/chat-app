package com.chat.client.presentation;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface CallbackDispatcher {
    <T>void addCallback(CompletableFuture<T> future, Consumer<T> onSuccess, Consumer<Throwable> onFailure);
}
