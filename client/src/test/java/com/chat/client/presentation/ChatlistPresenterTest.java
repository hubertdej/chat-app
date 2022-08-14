package com.chat.client.presentation;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.BDDMockito.*;

class ChatlistPresenterTest extends BaseTestCase {
    @Mock private ChatlistView view;
    @Mock private ChatlistPresenter.Factory factory;
    @Mock private UsersService usersService;
    @Mock private ChatsService chatsService;
    @Mock private ChatsRepository chatsRepository;
    @Mock private MessagingClient messagingClient;

    @InjectMocks private ChatlistPresenter presenter;

    @Test
    void testAddChat() {
        var chat = mock(Chat.class);

        presenter.addChat(chat);

        then(view).should().addChat(chat);
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

        presenter.openChat(chat);

        then(factory).should().openChatView(eq(chat), any());
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
