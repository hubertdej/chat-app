package com.chat.client.presentation;

import com.chat.client.domain.Conversation;
import com.chat.client.domain.Preview;

public class PresenterFactory implements OpeningFactory, AuthenticationPresenter.Factory {
    private final ViewFactory viewFactory;

    public PresenterFactory(ViewFactory viewFactory) { this.viewFactory = viewFactory; }

    public void openAuthenticationView() {
        var view = viewFactory.createAuthenticationView();
        var presenter = new AuthenticationPresenter(view, this);
        view.initialize(presenter);
        presenter.open();
    }

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
