package com.chat.client.domain;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.ChatsUpdater;
import com.chat.client.domain.application.Dispatcher;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class ChatsUpdaterTest extends BaseTestCase {
    @Mock private ChatsService chatsService;
    @Spy private CallbackDispatcher callbackDispatcher = new CallbackDispatcher(new Dispatcher() {
        @Override
        public void dispatch(Runnable runnable) {
            runnable.run();
        }
    });

    @InjectMocks private ChatsUpdater chatsUpdater;

    @Test
    void testHandleMessagesForExistingChat() {
        var uuid = UUID.randomUUID();
        var chat = mock(Chat.class);

        var chatsRepository = mock(ChatsRepository.class);
        given(chatsRepository.getByUUID(uuid)).willReturn(Optional.of(chat));

        var message1 = mock(Message.class);
        var message2 = mock(Message.class);

        chatsUpdater.handleMessages(uuid, chatsRepository, List.of(message1, message2));

        then(chat).should().addMessage(message1);
        then(chat).should().addMessage(message2);
        then(chat).shouldHaveNoMoreInteractions();
    }

    @Test
    void testHandleMessagesForNonexistentChat() {
        var uuid = UUID.randomUUID();
        var chat = mock(Chat.class);

        var chatsRepository = mock(ChatsRepository.class);
        given(chatsRepository.getByUUID(uuid)).willReturn(Optional.empty());

        given(chatsService.getChatDetails(uuid)).willReturn(CompletableFuture.completedFuture(chat));

        var message1 = mock(Message.class);
        var message2 = mock(Message.class);

        chatsUpdater.handleMessages(uuid, chatsRepository, List.of(message1, message2));

        then(chatsService).should().getChatDetails(uuid);
        then(chat).should().addMessage(message1);
        then(chat).should().addMessage(message2);
        then(chat).shouldHaveNoMoreInteractions();
    }

    @Test
    void testHandleMessagesFailedGetDetails() {
        var uuid = UUID.randomUUID();
        var chat = mock(Chat.class);

        var chatsRepository = mock(ChatsRepository.class);
        given(chatsRepository.getByUUID(uuid)).willReturn(Optional.empty());

        given(chatsService.getChatDetails(uuid)).willReturn(CompletableFuture.failedFuture(new Exception()));

        var message1 = mock(Message.class);
        var message2 = mock(Message.class);

        chatsUpdater.handleMessages(uuid, chatsRepository, List.of(message1, message2));
        then(chat).shouldHaveNoInteractions();
    }
}
