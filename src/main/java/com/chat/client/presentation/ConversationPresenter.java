package com.chat.client.presentation;


import com.chat.client.domain.Conversation;
import com.chat.client.domain.Message;

import java.sql.Timestamp;

public class ConversationPresenter {

    private final Conversation conversation;
    private final ConversationView view;
    private final PresenterFactory factory;

    public ConversationPresenter(
            Conversation conversation,
            ConversationView conversationView,
            PresenterFactory presenterFactory
    ) {
        this.conversation = conversation;
        this.view = conversationView;
        this.factory = presenterFactory;
    }

    void open() {
        for (Message message : conversation.getMessages()) {
            addMessage(message);
        }
        view.open();
    }

    public void close() {
        view.close();
    }

    public String conversationName() {
        return conversation.getName();
    }

    public void sendMessage(String text) {
        conversation.getMessages().add(new Message(text, new Timestamp(System.currentTimeMillis())));
        /* serverAdapter.sendMessage() */
        view.displayMessage(text);
        System.out.println(text);
    }
    private void addMessage(Message message) {
        view.addMessage(message.getText());
    }


}
