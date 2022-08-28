package com.chat.client.presentation;

import com.chat.client.domain.User;

public interface CreationView {
    void initialize(CreationPresenter presenter);
    void addUser(User user);
    void selectUser(User user);
    void unselectUser(User user);
    void filterUsers(String filter);
    void lockChanges();
    void unlockChanges();
    void enableCreation();
    void disableCreation();
    void indicateLoadingUsersFailed();
    void indicateChatCreationFailed();
    void open();
    void close();
}
