package com.chat.client.logic;

public interface ConversationView {
    void initialize(ConversationPresenter presenter);

    void displayMessage(String text);
    void addMessage(String text);
    void open();
    void close();
}
