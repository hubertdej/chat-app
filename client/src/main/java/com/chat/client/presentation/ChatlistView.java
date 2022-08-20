package com.chat.client.presentation;

import com.chat.client.domain.Chat;

public interface ChatlistView {
    void initialize(ChatlistPresenter chatlistPresenter);
    void addChat(Chat chat);
    void updateChat(Chat chat);
    void filterChats(String filter);
    void open();
    void close();
}
