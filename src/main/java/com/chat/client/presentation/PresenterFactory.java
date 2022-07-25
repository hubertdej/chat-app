package com.chat.client.presentation;

import com.chat.client.domain.*;
import com.chat.client.domain.application.AuthService;

public class PresenterFactory implements OpeningFactory, AuthPresenter.Factory {
    private final ViewFactory viewFactory;
    private final AuthService authService;
    private final CallbackDispatcher callbackDispatcher;

    public PresenterFactory(ViewFactory viewFactory,
                            AuthService authService,
                            CallbackDispatcher callbackDispatcher) {
        this.viewFactory = viewFactory;
        this.authService = authService;
        this.callbackDispatcher = callbackDispatcher;
    }

    public void openAuthView() {
        var view = viewFactory.createAuthView();
        var presenter = new AuthPresenter(view, this, authService, callbackDispatcher);
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
