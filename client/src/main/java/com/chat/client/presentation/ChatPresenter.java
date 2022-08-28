package com.chat.client.presentation;

import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatMessage;
import com.chat.client.domain.application.MessageSender;

public class ChatPresenter extends ChatPresenterHandle {
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

    private void addMessage(ChatMessage message) {
        view.addMessage(message);
    }

    private final Chat.Observer chatObserver = this::addMessage;

    public void open() {
        chat.addObserver(chatObserver);
        view.setTitle(chat.getName());
        view.displayChatMembers(chat.getMembers());
        view.open();
    }

    @Override
    public void close() {
        super.close();
        chat.removeObserver(chatObserver);
        view.close();
    }

    @Override
    public void focus() {
        view.focus();
    }
}
