package com.chat.client.presentation;

import com.chat.client.domain.Account;
import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.MessagingClient;

public class ChatlistPresenter {
    public interface Factory {
        void openAuthView();
        void openChatView(Account account, MessagingClient client, Chat chat);
        void openCreationView(ChatsRepository repository);

    }

    private final ChatlistView view;
    private final Factory factory;
    private final Account account;
    private final ChatsRepository chatsRepository;
    private final MessagingClient messagingClient;

    public ChatlistPresenter(ChatlistView view,
                             Factory factory,
                             Account account,
                             ChatsRepository chatsRepository,
                             MessagingClient messagingClient) {
        this.view = view;
        this.factory = factory;
        this.account = account;
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
        factory.openChatView(account, messagingClient, chat);
    }

    public void createChat() {
        factory.openCreationView(chatsRepository);
    }

    public void open() {
        chatsRepository.addObserver(this::addChat);
        messagingClient.initialize(account, chatsRepository);
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
