package com.chat.client.presentation;

import com.chat.client.domain.User;

public interface CreationView {
    void initialize(CreationPresenter presenter);
    void addUser(User user);
    void selectUser(User user);
    void unselectUser(User user);
    void filterUsers(String filter);
    void enableCreation();
    void disableCreation();
    void open();
    void close();
}
