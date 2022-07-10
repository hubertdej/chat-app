package com.chat.client.presentation;

import com.chat.client.domain.Conversation;
import com.chat.client.domain.Preview;

public class PreviewPresenter {
    private final Preview preview;
    private final LoggedView view;
    private final OpeningFactory factory;

    PreviewPresenter(Preview preview, LoggedView view, OpeningFactory factory) {
        this.preview = preview;
        this.view = view;
        this.factory = factory;
    }
    private final void addConversation(Conversation conversation) {
        view.addConversation(conversation.getName());
    }

    public void openConversationScene(int index) {
        Conversation conversation = preview.getConversations().get(index);
        factory.openConversationView(conversation);
    }

    void open() {
        for (Conversation conversation : preview.getConversations()) {
            addConversation(conversation);
        }
        view.open();
    }
    public void close() {
        view.close();
    }

}
