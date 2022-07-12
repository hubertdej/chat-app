package com.chat.client.presentation;

import com.chat.client.domain.*;

public class PresenterFactory implements OpeningFactory, AuthenticationPresenter.Factory {
    private final ViewFactory viewFactory;
    private final AccountRepository accountRepository;
    private final CallbackDispatcher callbackDispatcher;

    public PresenterFactory(ViewFactory viewFactory,
                            AccountRepository accountRepository,
                            CallbackDispatcher callbackDispatcher) {
        this.viewFactory = viewFactory;
        this.accountRepository = accountRepository;
        this.callbackDispatcher = callbackDispatcher;
    }

    public void openAuthenticationView() {
        var view = viewFactory.createAuthenticationView();
        var presenter = new AuthenticationPresenter(view, this, accountRepository, callbackDispatcher);
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
