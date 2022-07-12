package com.chat.client.javafxui;

import com.chat.client.presentation.CallbackDispatcher;
import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GuiCallbackDispatcher implements CallbackDispatcher {
    @Override
    public <T> void addCallback(CompletableFuture<T> future, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        future.whenComplete((result, throwable) -> {
            if (throwable == null) {
                Platform.runLater(() -> onSuccess.accept(result));
            } else {
                Platform.runLater(() -> onFailure.accept(throwable));
            }
        });
    }
}
