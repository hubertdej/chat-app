package com.chat.client.domain;

import java.util.ArrayList;
import java.util.List;

// TODO:
//  Should we try to generalize repositories?
//  This class is currently unused, as it is just a concept.
// TODO: Repositories should probably notify of updates to entire snapshots, not of individual snapshot elements.

public class ObservableRepository<T> {
    public interface Observer<T> {
        void notifyUpdate(T t);
    }

    protected final List<Observer<T>> observers = new ArrayList<>();
    protected final List<T> snapshot = new ArrayList<>();

    protected void forwardSnapshot(Observer<T> observer) {
        for (var item : snapshot) {
            observer.notifyUpdate(item);
        }
    }

    public void addObserver(Observer<T> observer) {
        observers.add(observer);
        forwardSnapshot(observer);
    }

    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    public void add(T t) {
        snapshot.add(t);
        for (var observer : observers) {
            observer.notifyUpdate(t);
        }
    }
}
