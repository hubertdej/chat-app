package com.chat.client.logic;

public interface LoggedView {
    void initialize(PreviewPresenter presenter);
    void addConversation(String name);
    void open();
    void close();
}
