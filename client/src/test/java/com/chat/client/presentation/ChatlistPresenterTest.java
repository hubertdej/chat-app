package com.chat.client.presentation;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatMessage;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.*;

class ChatlistPresenterTest extends BaseTestCase {
    @Mock private ChatlistView view;
    @Mock private ChatlistPresenter.Factory factory;
    @Mock private UsersService usersService;
    @Mock private ChatsService chatsService;
    @Mock private MessagingClient messagingClient;
    @Spy private final ChatsRepository chatsRepository = new ChatsRepository();

    @InjectMocks private ChatlistPresenter presenter;

    private ChatPresenterHandle mockHandle() {
        var handle = new ChatPresenterHandle() {
            @Override
            public void focus() {}
        };
        return spy(handle);
    }

    @Test
    void testChatsAreObserved() {
        var chat = new Chat(UUID.randomUUID(), "chat name", List.of());

        presenter.open();
        chatsRepository.addChat(chat);
        chat.addMessage(new ChatMessage(chat.getUUID(), "", null, null, false));
        presenter.close();
        chat.addMessage(new ChatMessage(chat.getUUID(), "", null, null, false));

        then(view).should(times(1)).updateChat(chat);
    }

    @Test
    void testChatsRepositoryIsObserved() {
        var chat1 = mock(Chat.class);
        var chat2 = mock(Chat.class);

        presenter.open();
        chatsRepository.addChat(chat1);
        presenter.close();
        chatsRepository.addChat(chat2);

        then(view).should().addChat(chat1);
        then(view).should(never()).addChat(chat2);
    }

    @Test
    void testFilterChats() {
        var filter = "filter";

        presenter.filterChats(filter);

        then(view).should().filterChats(any());
    }

    @Test
    void testOpenChat() {
        var chat = mock(Chat.class);
        var handle = mock(ChatPresenterHandle.class);
        given(factory.openChatView(any(), any())).willReturn(handle);

        presenter.openChat(chat);

        then(factory).should().openChatView(eq(chat), any());
    }

    @Test
    void testFocusChatAfterSecondOpen() {
        var chat = mock(Chat.class);
        var handle = mock(ChatPresenterHandle.class);
        given(factory.openChatView(any(), any())).willReturn(handle);

        presenter.openChat(chat);
        presenter.openChat(chat);

        then(handle).should(times(1)).focus();
    }

    @Test
    void testNoChatFocusAfterItWasClosed() {
        var chat = mock(Chat.class);
        var handle = mockHandle();
        given(factory.openChatView(any(), any())).willReturn(handle);

        presenter.openChat(chat);
        handle.close();
        presenter.openChat(chat);

        then(factory).should(times(2)).openChatView(eq(chat), any());
        then(handle).should(never()).focus();
    }

    @Test
    void testCreateChat() {
        presenter.createChat();

        then(factory).should().openCreationView(any(), any(), any());
    }

    @Test
    void testOpen() {
        presenter.open();

        then(view).should().open();
    }

    @Test
    void testClose() {
        presenter.close();

        then(view).should().close();
    }
}
