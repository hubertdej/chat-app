package com.chat.client.presentation;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.User;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.UsersService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

class CreationPresenterTest extends BaseTestCase {
    @Mock private CreationView view;
    @Mock private UsersService usersService;
    @Mock private ChatsService chatsService;
    @Mock private ChatsRepository chatsRepository;
    @Spy private final CallbackDispatcher callbackDispatcher = new CallbackDispatcher(Runnable::run);

    @InjectMocks private CreationPresenter presenter;

    @Test
    void testSuccessfulOpen() {
        var user = new User("");
        var future = CompletableFuture.completedFuture(List.of(user));
        given(usersService.getUsersAsync()).willReturn(future);

        presenter.open();

        then(view).should().disableCreation();
        then(view).should().open();
        then(view).should().addUser(user);
    }

    @Test
    void testUnsuccessfulOpen() {
        CompletableFuture<List<User>> future = CompletableFuture.failedFuture(new Exception());
        given(usersService.getUsersAsync()).willReturn(future);

        presenter.open();

        then(view).should().disableCreation();
        then(view).should().indicateLoadingUsersFailed();
        then(view).should().close();
    }

    @Test
    void testFilterUsers() {
        var filter = "filter";

        presenter.filterUsers(filter);

        then(view).should().filterUsers(any());
    }

    @Test
    void testSelectUser() {
        var user = new User("");

        presenter.selectUser(user);

        then(view).should().selectUser(user);
    }

    @Test
    void testUnselectUser() {
        var user = new User("");

        presenter.selectUser(user);
        presenter.unselectUser(user);

        then(view).should().unselectUser(user);
    }

    @Test
    void testCreationPossibility() {
        var user = new User("");
        
        presenter.selectUser(user);
        presenter.unselectUser(user);
        
        then(view).should(inOrder(view)).enableCreation();
        then(view).should(inOrder(view)).disableCreation();
    }

    @Test
    void testSuccessfulChatCreation() {
        var name = "name";
        var chat = mock(Chat.class);
        var future = CompletableFuture.completedFuture(chat);
        given(chatsService.createChatAsync(any(), any())).willReturn(future);

        presenter.createChat(name);

        then(chatsService).should().createChatAsync(eq(name), any());
        then(chatsRepository).should().addChat(chat);
        then(view).should().close();
    }

    @Test
    void testUnsuccessfulChatCreation() {
        var name = "name";
        CompletableFuture<Chat> future = CompletableFuture.failedFuture(new Exception());
        given(chatsService.createChatAsync(any(), any())).willReturn(future);

        presenter.createChat(name);

        then(chatsService).should().createChatAsync(eq(name), any());
        then(view).should().indicateChatCreationFailed();
    }

    @Test
    void testClose() {
        presenter.close();

        then(view).should().close();
    }
}
