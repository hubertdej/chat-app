package com.chat.client.domain.application;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CallbackDispatcher {
    private final Dispatcher dispatcher;

    public CallbackDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public <T> void addCallback(CompletableFuture<T> future, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        future.whenComplete((t, throwable) -> {
            if (throwable == null) {
                dispatcher.dispatch(() -> onSuccess.accept(t));
            } else {
                dispatcher.dispatch(() -> onFailure.accept(throwable));
            }
        });
    }
}
