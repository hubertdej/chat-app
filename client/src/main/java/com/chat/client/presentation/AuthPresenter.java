package com.chat.client.presentation;

import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.SessionManager;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;

public class AuthPresenter {
    public interface Factory {
        void openChatlistView(UsersService usersService, ChatsService chatsService, ChatsRepository chatsRepository, MessagingClient messagingClient);
    }

    private final AuthView view;
    private final Factory factory;
    private final SessionManager sessionManager;
    private final CallbackDispatcher callbackDispatcher;

    public AuthPresenter(AuthView view,
                         Factory factory,
                         SessionManager sessionManager,
                         CallbackDispatcher callbackDispatcher) {
        this.view = view;
        this.factory = factory;
        this.sessionManager = sessionManager;
        this.callbackDispatcher = callbackDispatcher;
    }

    public void open() {
        view.open();
    }

    public void login(String username, String password) {
        view.lockChanges();
        callbackDispatcher.addCallback(
                sessionManager.createSessionAsync(username, password),
                session -> {
                    factory.openChatlistView(
                            session.usersService(),
                            session.chatsService(),
                            session.chatsRepository(),
                            session.messagingClient()
                    );
                    view.close();
                },
                $ -> {
                    view.indicateLoginFailed();
                    view.unlockChanges();
                }
        );
    }

    public void register(String username, String password) {
        view.lockChanges();
        callbackDispatcher.addCallback(
                sessionManager.registerUserAsync(username, password),
                $ -> {
                    view.indicateRegistrationSuccessful();
                    view.unlockChanges();
                },
                $ -> {
                    view.indicateRegistrationFailed();
                    view.unlockChanges();
                }
        );
    }

    public void close() {
        view.close();
    }
}
