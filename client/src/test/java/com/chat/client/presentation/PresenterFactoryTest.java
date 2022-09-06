package com.chat.client.presentation;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.User;
import com.chat.client.domain.application.SessionManager;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.BDDMockito.*;

class PresenterFactoryTest extends BaseTestCase {
    @Mock private ViewFactory viewFactory;
    @Mock private SessionManager sessionManager;
    @Mock private CallbackDispatcher callbackDispatcher;

    @InjectMocks private PresenterFactory presenterFactory;

    @Test
    void testOpenAuthView() {
        var view = mock(AuthView.class);
        given(viewFactory.createAuthView()).willReturn(view);

        presenterFactory.openAuthView();

        then(viewFactory).should().createAuthView();
        then(view).should().initialize(any(AuthPresenter.class));
        then(view).should().open();
    }

    @Test
    void testOpenCreationView() {
        var view = mock(CreationView.class);
        given(viewFactory.createCreationView()).willReturn(view);

        presenterFactory.openCreationView(
                mock(UsersService.class),
                mock(ChatsService.class),
                mock(ChatsRepository.class)
        );

        then(viewFactory).should().createCreationView();
        then(view).should().initialize(any(CreationPresenter.class));
        then(view).should().open();
    }

    @Test
    void testOpenChatlistView() {
        var view = mock(ChatlistView.class);
        given(viewFactory.createChatlistView()).willReturn(view);

        presenterFactory.openChatlistView(
                mock(User.class),
                mock(UsersService.class),
                mock(ChatsService.class),
                mock(ChatsRepository.class),
                mock(MessagingClient.class)
        );

        then(viewFactory).should().createChatlistView();
        then(view).should().initialize(any(ChatlistPresenter.class));
        then(view).should().open();
    }

    @Test
    void testOpenChatView() {
        var view = mock(ChatView.class);
        given(viewFactory.createChatView()).willReturn(view);

        presenterFactory.openChatView(
                mock(Chat.class),
                mock(MessagingClient.class)
        );

        then(viewFactory).should().createChatView();
        then(view).should().initialize(any(ChatPresenter.class));
        then(view).should().open();
    }
}
