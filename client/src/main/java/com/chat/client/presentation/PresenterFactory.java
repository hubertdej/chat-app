package com.chat.client.presentation;

import com.chat.client.domain.*;
import com.chat.client.domain.application.*;

public class PresenterFactory implements AuthPresenter.Factory, ChatlistPresenter.Factory {
    private final ViewFactory viewFactory;
    private final AuthService authService;
    private final UsersService usersService;
    private final ChatsService chatsService;
    private final CallbackDispatcher callbackDispatcher;

    public PresenterFactory(ViewFactory viewFactory,
                            AuthService authService,
                            UsersService usersService,
                            ChatsService chatsService,
                            CallbackDispatcher callbackDispatcher) {
        this.viewFactory = viewFactory;
        this.authService = authService;
        this.usersService = usersService;
        this.chatsService = chatsService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Override
    public void openAuthView() {
        var view = viewFactory.createAuthView();
        var presenter = new AuthPresenter(view, this, authService, callbackDispatcher);
        view.initialize(presenter);
        presenter.open();
    }

    @Override
    public void openCreationView(ChatsRepository repository) {
        var view = viewFactory.createCreationView();
        var presenter = new CreationPresenter(view, usersService, chatsService, repository, callbackDispatcher);
        view.initialize(presenter);
        presenter.open();
    }

    @Override
    public void openChatlistView(Account account) {
        var view = viewFactory.createChatlistView();
        var presenter = new ChatlistPresenter(
                view,
                this,
                account,
                account.getRepository(),//TODO remove?
                account.getClient()//TODO remove?
        );
        view.initialize(presenter);
        presenter.open();
    }

    @Override
    public void openChatView(Account account, Chat chat) {
        var view = viewFactory.createChatView();
        var presenter = new ChatPresenter(view, account, chat, account.getClient());
        view.initialize(presenter);
        presenter.open();
    }
}
