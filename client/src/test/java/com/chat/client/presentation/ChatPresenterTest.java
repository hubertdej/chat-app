package com.chat.client.presentation;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.Chat;
import com.chat.client.domain.Message;
import com.chat.client.domain.User;
import com.chat.client.domain.application.MessageSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

public class ChatPresenterTest extends BaseTestCase {
    @Mock private ChatView view;
    @Spy  private Chat chat = new Chat(UUID.randomUUID(), "chat-name", List.of(new User("user")));
    @Mock private MessageSender messageSender;

    @InjectMocks private ChatPresenter presenter;

    @Test
    void testSendMessage() {
        var text = "sample-message";
        var messageCaptor = ArgumentCaptor.forClass(String.class);

        presenter.sendMessage(text);

        then(messageSender).should().sendMessage(eq(chat.getUUID()), messageCaptor.capture());
        assertEquals(messageCaptor.getValue(), text);
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

    @Test
    void testFocus() {
        presenter.focus();

        then(view).should().focus();
    }

    @Test
    void testChatIsObserved() {
        var message1 = mock(Message.class);
        var message2 = mock(Message.class);

        presenter.open();
        chat.addMessage(message1);
        presenter.close();
        chat.addMessage(message2);

        then(view).should().addMessage(message1);
        then(view).should(never()).addMessage(message2);

    }
}
