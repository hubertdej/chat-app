package com.chat.client.domain.application;

public interface Dispatcher {
    void dispatch(Runnable runnable);
}
