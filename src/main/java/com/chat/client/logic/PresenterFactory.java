package com.chat.client.logic;

import com.chat.client.model.Conversation;
import com.chat.client.model.Preview;

public class PresenterFactory implements OpeningFactory {
    private final ViewFactory viewFactory;

    public PresenterFactory(ViewFactory viewFactory) { this.viewFactory = viewFactory; }

    @Override
    public void openLoggedView(Preview preview) {
        LoggedView loggedView = viewFactory.createLoggedView();
        PreviewPresenter presenter = new PreviewPresenter(preview, loggedView, this);
        loggedView.initialize(presenter);
        presenter.open();
    }

    @Override
    public void openConversationView(Conversation conversation) {
        ConversationView conversationView = viewFactory.createConversationView();
        ConversationPresenter presenter = new ConversationPresenter(conversation, conversationView, this);
        conversationView.initialize(presenter);
        presenter.open();
    }
}
