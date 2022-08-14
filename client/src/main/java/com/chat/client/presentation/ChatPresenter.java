package com.chat.client.presentation;

import com.chat.client.domain.Account;
import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatMessage;
import com.chat.client.domain.User;
import com.chat.client.domain.application.MessageSender;
import com.chat.client.domain.application.MessagingClient;

public class ChatPresenter {
    private final ChatView view;
    private final Account account;
    private final Chat chat;
    private final MessageSender messageSender;

    public ChatPresenter(ChatView view, Account account, Chat chat, MessageSender messageSender) {
        this.view = view;
        this.account = account;
        this.chat = chat;
        this.messageSender = messageSender;
    }

    public boolean isUs(User user) {
        // TODO: Add equality checking to User class.
        return user.name().equals(account.getUser().name());
    }

    public void sendMessage(String text) {
        messageSender.sendMessage(chat, text);
    }

    public void open() {
        chat.addObserver(view::addMessage);
        view.setTitle(chat.getName());
        view.open();
    }

    public void close() {
        chat.removeObserver(view::addMessage);
        view.close();
    }
}
