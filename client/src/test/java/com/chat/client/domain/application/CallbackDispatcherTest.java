package com.chat.client.domain.application;

import com.chat.client.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;

class TrivialDispatcher implements Dispatcher {
    @Override
    public void dispatch(Runnable runnable) {
        runnable.run();
    }
}

public class CallbackDispatcherTest extends BaseTestCase {
    @Spy private final Dispatcher dispatcher = new TrivialDispatcher();

    @InjectMocks private CallbackDispatcher callbackDispatcher;

    private <T> Consumer<T> mockConsumer() {
        return spy(new Consumer<T>() { @Override public void accept(T t) {} });
    }

    @Test
    void testAddCallbackWithSuccessfulFuture() {
        var object = new Object();
        var successfulFuture = CompletableFuture.completedFuture(object);
        Consumer<Object> onSuccess = mockConsumer();
        Consumer<Throwable> onFailure = mockConsumer();

        callbackDispatcher.addCallback(successfulFuture, onSuccess, onFailure);

        then(onSuccess).should().accept(object);
        then(onFailure).should(never()).accept(any());
    }

    @Test
    void testAddCallbackWithUnsuccessfulFuture() {
        var throwable = new Exception();
        var unsuccessfulFuture = CompletableFuture.failedFuture(throwable);
        Consumer<Object> onSuccess = mockConsumer();
        Consumer<Throwable> onFailure = mockConsumer();

        callbackDispatcher.addCallback(unsuccessfulFuture, onSuccess, onFailure);

        then(onFailure).should().accept(throwable);
        then(onSuccess).should(never()).accept(any());
    }
}
