package com.chat.client.local;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatMessage;
import com.chat.client.domain.User;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.registration.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class LocalChatsServiceTest extends BaseTestCase {
    @Mock private ConversationStorageFacade storageFacade;
    @Mock private User localUser;
    @InjectMocks LocalChatsService service;

    @Test
    void testCreateChatAsync() throws ExecutionException, InterruptedException {
        var username = "Alice";
        var bob = "Bob";
        var bobUser = new User(bob);
        var id = new UUID(12, 34);
        var chatName = "bff";
        given(localUser.name()).willReturn(username);
        given(storageFacade.add(any(String.class), any(List.class))).willReturn(id);
        var members = List.of(bobUser, localUser);

        var chat = service.createChatAsync(chatName, List.of(bobUser)).get();

        then(storageFacade).should().add(chatName, List.of(bob, username));
        assertEquals(chatName, chat.getName());
        assertEquals(id, chat.getUUID());
        assertEquals(members, chat.getMembers());
        assertFalse(chat.hasMessages());
    }

    @Test
    void testChatDetailsFromExistingId() throws ExecutionException, InterruptedException {
        var id = new UUID(12, 34);
        var username = "Alice";
        var friend = "Bob";
        var chatName = "bff";
        var membersAsUsers = List.of(new User(friend), new User(username));
        var members = List.of(friend, username);
        var timestamp = new Timestamp(1);
        var messageDtos = List.of(new MessageDto(username, id, "hey", timestamp));
        var optional = Optional.of(new ConversationDto(id, chatName, members, messageDtos));
        given(storageFacade.get(id)).willReturn(optional);

        var chat = service.getChatDetails(id).get();

        then(storageFacade).should().get(id);
        assertEquals(chatName, chat.getName());
        assertEquals(id, chat.getUUID());
        assertEquals(membersAsUsers, chat.getMembers());
    }

    @Test
    void testNonExistingChatDetails() throws InterruptedException {
        var id = new UUID(12, 34);
        Optional<ConversationDto> optional = Optional.empty();
        given(storageFacade.get(any())).willReturn(optional);

        try {
           service.getChatDetails(id).get();
        } catch (ExecutionException e) {
            assertThrows(NoSuchElementException.class, () -> { throw e.getCause(); });
        }

        then(storageFacade).should().get(id);
    }
}