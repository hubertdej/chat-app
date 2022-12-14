package com.chat.client.presentation;

import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.User;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.SessionManager;
import com.chat.client.domain.application.UsersService;
import com.chat.client.validators.Validator;

public class PresenterFactory implements AuthPresenter.Factory, ChatlistPresenter.Factory {
    private final ViewFactory viewFactory;
    private final SessionManager sessionManager;
    private final CallbackDispatcher callbackDispatcher;
    private final Validator<String> usernameValidator;
    private final Validator<String> passwordValidator;
    private final Validator<String> chatNameValidator;

    public PresenterFactory(ViewFactory viewFactory,
                            SessionManager sessionManager,
                            CallbackDispatcher callbackDispatcher,
                            Validator<String> usernameValidator,
                            Validator<String> passwordValidator,
                            Validator<String> chatNameValidator) {
        this.viewFactory = viewFactory;
        this.sessionManager = sessionManager;
        this.callbackDispatcher = callbackDispatcher;
        this.usernameValidator = usernameValidator;
        this.passwordValidator = passwordValidator;
        this.chatNameValidator = chatNameValidator;
    }

    @Override
    public void openAuthView() {
        var view = viewFactory.createAuthView();
        var presenter = new AuthPresenter(view, this, sessionManager, callbackDispatcher, usernameValidator, passwordValidator);
        view.initialize(presenter);
        presenter.open();
    }

    @Override
    public void openCreationView(
            UsersService usersService,
            ChatsService chatsService,
            ChatsRepository chatsRepository
    ) {
        var view = viewFactory.createCreationView();
        var presenter = new CreationPresenter(view, usersService, chatsService, chatsRepository, callbackDispatcher, chatNameValidator);
        view.initialize(presenter);
        presenter.open();
    }

    @Override
    public void openChatlistView(
            User user,
            UsersService usersService,
            ChatsService chatsService,
            ChatsRepository chatsRepository,
            MessagingClient messagingClient
    ) {
        var view = viewFactory.createChatlistView();
        var presenter = new ChatlistPresenter(view, this, user, usersService, chatsService, chatsRepository, messagingClient);
        view.initialize(presenter);
        presenter.open();
    }

    @Override
    public ChatPresenterHandle openChatView(Chat chat, MessagingClient client) {
        var view = viewFactory.createChatView();
        var presenter = new ChatPresenter(view, chat, client);
        view.initialize(presenter);
        presenter.open();
        return presenter;
    }
}
