package com.chat.client.presentation;

import java.util.ArrayList;
import java.util.List;

public abstract class ChatPresenterHandle {
    interface OnCloseObserver {
        void notifyOnClose();
    }

    private final List<OnCloseObserver> onCloseObservers = new ArrayList<>();

    public void addOnCloseObserver(ChatPresenterHandle.OnCloseObserver observer) {
        onCloseObservers.add(observer);
    }

    public void close() {
        for (var observer : onCloseObservers) {
            observer.notifyOnClose();
        }
    }

    public abstract void focus();
}
