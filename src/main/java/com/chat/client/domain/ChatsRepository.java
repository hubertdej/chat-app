package com.chat.client.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatsRepository {
    public interface Observer {
        void notifyUpdate(Chat chat);
    }

    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private final List<Chat> snapshot = new ArrayList<>();

    public void addChat(String name) {
        var chat = new Chat(name);
        snapshot.add(chat);
        for (var observer : observers) {
            observer.notifyUpdate(chat);
        }
    }

    public Optional<Chat> getByName(String name) {
        return snapshot.stream().filter(chat -> chat.getName().equals(name)).findFirst();
    }
}
