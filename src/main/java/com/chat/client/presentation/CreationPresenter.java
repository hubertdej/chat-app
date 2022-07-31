package com.chat.client.presentation;

import com.chat.client.domain.*;

import java.util.*;
import java.util.stream.Collectors;

public class CreationPresenter {
    private final CreationView view;
    private final ChatsRepository chatsRepository;

    public CreationPresenter(CreationView view, ChatsRepository chatsRepository) {
        this.view = view;
        this.chatsRepository = chatsRepository;
    }

    public void filterUsers(String filter) {
        view.filterUsers(filter);
    }

    private final Set<User> selectedUsers = new TreeSet<>(Comparator.comparing(User::name));

    public void selectUser(User user) {
        if (selectedUsers.add(user)) {
            view.selectUser(user);
        }
        if (!selectedUsers.isEmpty()) {
            view.enableCreation();
        }
    }

    public void unselectUser(User user) {
        if (selectedUsers.remove(user)) {
            view.unselectUser(user);
        }
        if (selectedUsers.isEmpty()) {
            view.disableCreation();
        }
    }

    public void createChat() {
        var usernames = selectedUsers.stream().map(User::name).collect(Collectors.toList());
        chatsRepository.addChat(String.join(", ", usernames));
        view.close();
    }

    public void open() {
        view.open();
        view.addUser(new User("Aska"));
        view.addUser(new User("Bartolomeu"));
        view.addUser(new User("Cecil"));
        view.addUser(new User("Dorothy"));
    }

    public void close() {
        view.close();
    }
}
