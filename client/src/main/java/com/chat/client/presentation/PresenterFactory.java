package com.chat.client.presentation;

import com.chat.client.domain.*;
import com.chat.client.domain.application.*;

import java.util.function.Supplier;

public class PresenterFactory implements OpeningFactory, AuthPresenter.Factory, ChatlistPresenter.Factory {
    private final ViewFactory viewFactory;
    private final AuthService authService;
    private final UsersService usersService;
    private final ChatsService chatsService;
    private final CallbackDispatcher callbackDispatcher;
    private final Supplier<ChatsRepository> chatsRepositorySupplier;
    private final Supplier<MessagingClient> messagingClientSupplier;

    public PresenterFactory(ViewFactory viewFactory,
                            AuthService authService,
                            UsersService usersService,
                            ChatsService chatsService,
                            CallbackDispatcher callbackDispatcher, Supplier<ChatsRepository> chatsRepositorySupplier, Supplier<MessagingClient> messagingClientSupplier) {
        this.viewFactory = viewFactory;
        this.authService = authService;
        this.usersService = usersService;
        this.chatsService = chatsService;
        this.callbackDispatcher = callbackDispatcher;
        this.chatsRepositorySupplier = chatsRepositorySupplier;
        this.messagingClientSupplier = messagingClientSupplier;
    }

    @Override
    public void openAuthView() {
        var view = viewFactory.createAuthView();
        var presenter = new AuthPresenter(view, this, authService, callbackDispatcher);
        view.initialize(presenter);
        presenter.open();
    }

    @Override
    public void openCreationView(ChatsRepository chatsRepository) {
        var view = viewFactory.createCreationView();
        var presenter = new CreationPresenter(view, usersService, chatsService, chatsRepository, callbackDispatcher);
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
                chatsRepositorySupplier.get(),
                messagingClientSupplier.get());
        view.initialize(presenter);
        presenter.open();
    }

    @Override
    public void openChatView(Account account, MessagingClient client, Chat chat) {
        var view = viewFactory.createChatView();
        var presenter = new ChatPresenter(view, account, chat, client);
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
