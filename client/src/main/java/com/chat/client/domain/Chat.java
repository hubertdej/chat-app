package com.chat.client.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Chat implements Comparable<Chat> {
    public interface Observer {
        void notifyUpdate(ChatMessage message);
    }

    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
        for (var message : snapshot) { observer.notifyUpdate(message); }
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

    private final UUID uuid;
    private final String name;
    private final List<User> recipients;

    public Chat(UUID uuid, String name, List<User> recipients) {
        this.uuid = uuid;
        this.name = name;
        this.recipients = recipients;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean hasMessages() {
        return !snapshot.isEmpty();
    }

    public ChatMessage getLastMessage() {
        return snapshot.get(snapshot.size() - 1);
    }

    @Override
    public int compareTo(Chat other) {
        if (!this.hasMessages() && !other.hasMessages()) {
            return this.getName().compareTo(other.getName());
        }
        if (!this.hasMessages()) {
            return -1;
        }
        if (!other.hasMessages()) {
            return 1;
        }
        return other.getLastMessage().timestamp().compareTo(this.getLastMessage().timestamp());
    }
}
