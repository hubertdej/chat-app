package com.chat.client.presentation;

import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;

public class ChatlistPresenter {
    public interface Factory {
        void openAuthView();
        void openChatView(Chat chat, MessagingClient client);
        void openCreationView(UsersService usersService, ChatsService chatsService, ChatsRepository chatsRepository);
    }

    private final ChatlistView view;
    private final Factory factory;
    private final UsersService usersService;
    private final ChatsService chatsService;
    private final ChatsRepository chatsRepository;
    private final MessagingClient messagingClient;

    public ChatlistPresenter(ChatlistView view,
                             Factory factory,
                             UsersService usersService,
                             ChatsService chatsService,
                             ChatsRepository chatsRepository,
                             MessagingClient messagingClient) {
        this.view = view;
        this.factory = factory;
        this.usersService = usersService;
        this.chatsService = chatsService;
        this.chatsRepository = chatsRepository;
        this.messagingClient = messagingClient;
    }

    public void addChat(Chat chat) {
        view.addChat(chat);
    }

    public void filterChats(String filter) {
        view.filterChats(filter);
    }

    public void openChat(Chat chat) {
        factory.openChatView(chat, messagingClient);
    }

    public void createChat() {
        factory.openCreationView(usersService, chatsService, chatsRepository);
    }

    public void open() {
        chatsRepository.addObserver(this::addChat);
        messagingClient.initialize();
        view.open();
    }

    public void close() {
        chatsRepository.removeObserver(this::addChat);
        messagingClient.close();
        factory.openAuthView();
        view.close();
        // TODO: All other windows should close too.
    }
}
