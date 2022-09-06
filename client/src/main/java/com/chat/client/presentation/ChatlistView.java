package com.chat.client.presentation;

import com.chat.client.domain.Chat;
import com.chat.client.domain.User;

public interface ChatlistView {
    void initialize(ChatlistPresenter chatlistPresenter);
    void addChat(Chat chat);
    void updateChat(Chat chat);
    void filterChats(String filter);
    void displayWelcomeMessage(User user);
    void open();
    void close();
}
