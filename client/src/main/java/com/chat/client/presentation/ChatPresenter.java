package com.chat.client.presentation;

import com.chat.client.domain.Chat;
import com.chat.client.domain.application.MessageSender;

public class ChatPresenter {
    private final ChatView view;
    private final Chat chat;
    private final MessageSender messageSender;

    public ChatPresenter(ChatView view, Chat chat, MessageSender messageSender) {
        this.view = view;
        this.chat = chat;
        this.messageSender = messageSender;
    }

    public void sendMessage(String text) {
        messageSender.sendMessage(chat.getUUID(), text);
    }

    public void open() {
        chat.addObserver(view::addMessage);
        view.setTitle(chat.getName());
        view.displayChatMembers(chat.getMembers());
        view.open();
    }

    public void close() {
        chat.removeObserver(view::addMessage);
        view.close();
    }
}
