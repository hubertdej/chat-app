package com.chat.client.domain;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public interface Observer {
        void notifyUpdate(ChatMessage observer);
    }

    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private final List<ChatMessage> snapshot = new ArrayList<>();

    public void addMessage(ChatMessage message) {
        snapshot.add(message);
        for (var observer : observers) {
            observer.notifyUpdate(message);
        }
    }

    private final String name;

    public Chat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
