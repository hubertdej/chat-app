package com.chat.client.local;

import com.chat.client.domain.*;
import com.chat.client.domain.application.AuthService;
import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.listconversationids.dto.ListConversationsRequestDto;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.dto.UserDto;
import com.chat.server.domain.registration.dto.UsernameTakenException;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocalAuthService implements AuthService {
    private final AuthenticationFacade authenticator;
    private final RegistrationFacade registrar;
    private final SessionStorageFacade sessionsManager;
    private final ListUserConversationsFacade conversationsLoader;
    private MessageReceiverFacade messageReceiver;

    public LocalAuthService(
            AuthenticationFacade authenticationFacade,
            RegistrationFacade registrationFacade,
            SessionStorageFacade sessionStorageFacade,
            ListUserConversationsFacade userConversationsFacade,
            MessageReceiverFacade messageReceiverFacade
    ) {
        this.authenticator = authenticationFacade;
        this.registrar = registrationFacade;
        this.sessionsManager = sessionStorageFacade;
        this.conversationsLoader = userConversationsFacade;
        this.messageReceiver = messageReceiverFacade;
    }
    @Override
    public CompletableFuture<Void> registerUserAsync(String username, String password) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        registrar.register(new UserDto(username, password));
                    } catch (UsernameTakenException e) {
                        throw new AuthFailedException();
                    }
                    return null;
                }
        );
    }
    @Override
    public CompletableFuture<Account> loginUserAsync(String username, String password) {
        //TODO instead of this implementation one could use messageReceiverFacade to get list of conversations
        // HOWEVER it would be necessary to create ServerMessagingSession here instead of inside initialize() in
        // LocalMessageClient in order to add session to the map inside sessionsManager.
        // Do we want to get list of conversations anytime besides when logging?
        return CompletableFuture.supplyAsync(
                () -> {
                    if(authenticator.authenticate(username, password)) {
                        ChatsRepository repository = new ChatsRepository();
                        var chatss = conversationsLoader.listConversations(
                                new ListConversationsRequestDto(username, new HashMap<>())
                        );
                        System.out.println("ok");
                        var chats = chatss.stream().map(dto -> new Chat(
                                dto.getConversationId(),
                                dto.getName(),
                                dto.getMembers().stream().map(User::new).toList())
                        ).toList();
                        for (Chat chat : chats) repository.addChat(chat);
                        return new Account(
                                new User(username),
                                password,
                                repository,
                                new LocalMessageClient(sessionsManager, messageReceiver)
                        );
                    } else {
                        System.out.println(password);
                        throw new AuthFailedException();
                    }
                }
        );
    }
}
