package com.chat.client.presentation;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.Chat;
import com.chat.client.domain.application.MessageSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

public class ChatPresenterTest extends BaseTestCase {
    @Mock private ChatView view;
    @Mock private Chat chat;
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
}
