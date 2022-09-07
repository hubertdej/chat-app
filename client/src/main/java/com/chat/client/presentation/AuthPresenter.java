package com.chat.client.presentation;

import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.User;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.SessionManager;
import com.chat.client.domain.application.UsersService;
import com.chat.client.validators.ValidationException;
import com.chat.client.validators.Validator;

public class AuthPresenter {
    public interface Factory {
        void openChatlistView(User user, UsersService usersService, ChatsService chatsService, ChatsRepository chatsRepository, MessagingClient messagingClient);
    }

    private final AuthView view;
    private final Factory factory;
    private final SessionManager sessionManager;
    private final CallbackDispatcher callbackDispatcher;
    private final Validator<String> usernameValidator;
    private final Validator<String> passwordValidator;

    public AuthPresenter(AuthView view,
                         Factory factory,
                         SessionManager sessionManager,
                         CallbackDispatcher callbackDispatcher,
                         Validator<String> usernameValidator,
                         Validator<String> passwordValidator) {
        this.view = view;
        this.factory = factory;
        this.sessionManager = sessionManager;
        this.callbackDispatcher = callbackDispatcher;
        this.usernameValidator = usernameValidator;
        this.passwordValidator = passwordValidator;
    }

    public void open() {
        view.open();
    }

    private void validateInput(String username, String password) throws ValidationException {
        usernameValidator.validate(username);
        passwordValidator.validate(password);
    }

    public void login(String username, String password) {
        try {
            validateInput(username, password);
        } catch (ValidationException e) {
            view.indicateLoginFailed(e.getMessage());
            return;
        }

        view.lockChanges();
        callbackDispatcher.addCallback(
                sessionManager.createSessionAsync(username, password),
                session -> {
                    factory.openChatlistView(
                            session.localUser(),
                            session.usersService(),
                            session.chatsService(),
                            session.chatsRepository(),
                            session.messagingClient()
                    );
                    view.close();
                },
                $ -> {
                    view.indicateLoginFailed("");
                    view.unlockChanges();
                }
        );
    }

    public void register(String username, String password) {
        try {
            validateInput(username, password);
        } catch (ValidationException e) {
            view.indicateRegistrationFailed(e.getMessage());
            return;
        }

        view.lockChanges();
        callbackDispatcher.addCallback(
                sessionManager.registerUserAsync(username, password),
                $ -> {
                    view.indicateRegistrationSuccessful();
                    view.unlockChanges();
                },
                $ -> {
                    view.indicateRegistrationFailed("");
                    view.unlockChanges();
                }
        );
    }

    public void close() {
        view.close();
    }
}
