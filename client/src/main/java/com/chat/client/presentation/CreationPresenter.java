package com.chat.client.presentation;

import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.User;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.UsersService;
import com.chat.client.validators.ValidationException;
import com.chat.client.validators.Validator;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class CreationPresenter {
    private final CreationView view;
    private final UsersService usersService;
    private final ChatsService chatsService;
    private final ChatsRepository chatsRepository;
    private final CallbackDispatcher callbackDispatcher;
    private final Validator<String> chatNameValidator;

    public CreationPresenter(CreationView view,
                             UsersService usersService,
                             ChatsService chatsService,
                             ChatsRepository chatsRepository,
                             CallbackDispatcher callbackDispatcher,
                             Validator<String> chatNameValidator) {
        this.view = view;
        this.usersService = usersService;
        this.chatsService = chatsService;
        this.chatsRepository = chatsRepository;
        this.callbackDispatcher = callbackDispatcher;
        this.chatNameValidator = chatNameValidator;
    }

    public void filterUsers(String filter) {
        view.filterUsers(filter);
    }

    private final Set<User> selectedUsers = new TreeSet<>(Comparator.comparing(User::name));

    public void selectUser(User user) {
        if (selectedUsers.add(user)) {
            view.selectUser(user);
        }
        if (!selectedUsers.isEmpty()) {
            view.enableCreation();
        }
    }

    public void unselectUser(User user) {
        if (selectedUsers.remove(user)) {
            view.unselectUser(user);
        }
        if (selectedUsers.isEmpty()) {
            view.disableCreation();
        }
    }

    public void createChat(String name) {
        try {
            chatNameValidator.validate(name);
        } catch (ValidationException e) {
            view.indicateChatCreationFailed(e.getMessage());
            view.unlockChanges();
            return;
        }

        var recipients = selectedUsers.stream().toList();
        view.lockChanges();
        callbackDispatcher.addCallback(
                chatsService.createChatAsync(name, recipients),
                chat -> {
                    chatsRepository.addChat(chat);
                    view.close();
                },
                $ -> {
                    view.indicateChatCreationFailed("");
                    view.unlockChanges();
                }
        );
    }

    public void open() {
        view.disableCreation();
        view.open();

        callbackDispatcher.addCallback(
                usersService.getUsersAsync(),
                users -> users.forEach(view::addUser),
                $ -> {
                    view.indicateLoadingUsersFailed();
                    view.close();
                }
        );
    }

    public void close() {
        view.close();
    }
}
