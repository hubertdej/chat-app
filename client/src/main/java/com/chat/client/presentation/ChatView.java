package com.chat.client.presentation;

import com.chat.client.domain.Message;
import com.chat.client.domain.User;

import java.util.List;

public interface ChatView {
    void initialize(ChatPresenter presenter);
    void addMessage(Message message);
    void setTitle(String title);
    void displayChatMembers(List<User> members);
    void open();
    void close();
    void focus();
}
