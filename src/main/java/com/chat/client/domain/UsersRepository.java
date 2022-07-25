package com.chat.client.domain;

import java.util.ArrayList;
import java.util.List;


public class UsersRepository {
    public interface Observer {
        void notifyUpdate(User user);
    }

    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
        forwardSnapshot(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private final List<User> snapshot = new ArrayList<>();

    private void forwardSnapshot(Observer observer) {
        for (var item : snapshot) {
            observer.notifyUpdate(item);
        }
    }

    public UsersRepository() {
        // TODO: Load a snapshot from database?
        snapshot.add(new User("Aska"));
        snapshot.add(new User("Bartolomeu"));
        snapshot.add(new User("Cecil"));
        snapshot.add(new User("Dorothy"));
    }
}
