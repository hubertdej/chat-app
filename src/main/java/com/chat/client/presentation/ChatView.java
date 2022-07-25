package com.chat.client.presentation;

import com.chat.client.domain.ChatMessage;

public interface ChatView {
    void initialize(ChatPresenter presenter);
    void addMessage(ChatMessage message);
    void setTitle(String title);
    void open();
    void close();
}
