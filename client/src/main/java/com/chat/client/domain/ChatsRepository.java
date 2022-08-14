package com.chat.client.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public void addChat(Chat chat) {
        snapshot.add(chat);
        for (var observer : observers) {
            observer.notifyUpdate(chat);
        }
    }

    public List<Chat> getChats() {
        return Collections.unmodifiableList(snapshot);
    }

    public Optional<Chat> getByUUID(UUID uuid) {
        return snapshot.stream().filter(chat -> chat.getUUID().equals(uuid)).findFirst();
    }
}
