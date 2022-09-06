package com.chat.client.presentation;

import com.chat.client.domain.Chat;
import com.chat.client.domain.Message;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatlistPresenter {
    public interface Factory {
        void openAuthView();
        ChatPresenterHandle openChatView(Chat chat, MessagingClient client);
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

    private final ChatsRepository.Observer chatsRepositoryObserver = this::addChat;
    private final Chat.Observer chatUpdateObserver = this::updateChat;

    private void addChat(Chat chat) {
        view.addChat(chat);
        chat.addObserver(chatUpdateObserver, true);
    }

    private void updateChat(Message message) {
        var chat = chatsRepository.getByUUID(message.chatUUID()).orElseThrow();
        view.updateChat(chat);
    }

    public void filterChats(String filter) {
        view.filterChats(filter);
    }

    private final Map<Chat, ChatPresenterHandle> chatHandles = new HashMap<>();

    public void openChat(Chat chat) {
        if (chatHandles.containsKey(chat)) {
            chatHandles.get(chat).focus();
            return;
        }
        var handle = factory.openChatView(chat, messagingClient);
        handle.addOnCloseObserver(() -> chatHandles.remove(chat));
        chatHandles.put(chat, handle);
    }

    public void createChat() {
        factory.openCreationView(usersService, chatsService, chatsRepository);
    }

    public void open() {
        chatsRepository.addObserver(chatsRepositoryObserver);
        messagingClient.initialize();
        view.open();
    }

    private List<ChatPresenterHandle> getHandles() {
        return new ArrayList<>(chatHandles.values());
    }

    public void close() {
        chatsRepository.removeObserver(chatsRepositoryObserver);
        chatsRepository.getChats().forEach(chat -> chat.removeObserver(chatUpdateObserver));
        messagingClient.close();
        factory.openAuthView();
        getHandles().forEach(ChatPresenterHandle::close);
        view.close();
    }
}
