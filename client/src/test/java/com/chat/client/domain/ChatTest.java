package com.chat.client.domain;

import com.chat.client.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

public class ChatTest extends BaseTestCase {
    private final UUID uuid = UUID.randomUUID();
    private final String name = "chat-name";
    private final List<User> members = List.of(mock(User.class), mock(User.class));

    private Chat chat;

    @BeforeEach
    void setup() {
        chat = new Chat(uuid, name, members);
    }

    @Test
    void testCreate() {
        assertEquals(uuid, chat.getUUID());
        assertEquals(name, chat.getName());
        assertThat(chat.getMembers()).containsAll(members);
        assertFalse(chat.hasMessages());
        assertThrows(Throwable.class, chat::getLastMessage);
    }

    @Test
    void testAddMessage() {
        var message1 = mock(ChatMessage.class);
        var message2 = mock(ChatMessage.class);

        chat.addMessage(message1);
        chat.addMessage(message2);

        assertTrue(chat.hasMessages());
        assertEquals(message2, chat.getLastMessage());
    }

    @Test
    void testObserverWithInitialValues() {
        var observer = mock(Chat.Observer.class);
        var messageBeforeAdd = mock(ChatMessage.class);
        var messageAfterAdd = mock(ChatMessage.class);
        var messageAfterRemove = mock(ChatMessage.class);

        chat.addMessage(messageBeforeAdd);
        chat.addObserver(observer, true);
        chat.addMessage(messageAfterAdd);
        chat.removeObserver(observer);
        chat.addMessage(messageAfterRemove);

        then(observer).should(times(1)).notifyUpdate(messageBeforeAdd);
        then(observer).should(times(1)).notifyUpdate(messageAfterAdd);
        then(observer).shouldHaveNoMoreInteractions();
    }

    @Test
    void testObserverWithoutInitialValues() {
        var observer = mock(Chat.Observer.class);
        var messageBeforeAdd = mock(ChatMessage.class);
        var messageAfterAdd = mock(ChatMessage.class);
        var messageAfterRemove = mock(ChatMessage.class);

        chat.addMessage(messageBeforeAdd);
        chat.addObserver(observer, false);
        chat.addMessage(messageAfterAdd);
        chat.removeObserver(observer);
        chat.addMessage(messageAfterRemove);

        then(observer).should(never()).notifyUpdate(messageBeforeAdd);
        then(observer).should(times(1)).notifyUpdate(messageAfterAdd);
        then(observer).shouldHaveNoMoreInteractions();
    }

    // Chat with no messages are compared by name.
    @Test
    void testCompareWhenBothHaveNoMessages() {
        var chat1 = new Chat(uuid, "1", List.of());
        var chat2 = new Chat(uuid, "2", List.of());

        assertThat(chat1.compareTo(chat2)).isLessThan(0);
        assertThat(chat2.compareTo(chat1)).isGreaterThan(0);
    }

    // Chat with no messages have bigger priority.
    @Test
    void testCompareWhenOneHasMessages() {
        var chat1 = new Chat(uuid, name, members);
        var chat2 = new Chat(uuid, name, members);

        chat2.addMessage(mock(ChatMessage.class));

        assertThat(chat1.compareTo(chat2)).isLessThan(0);
        assertThat(chat2.compareTo(chat1)).isGreaterThan(0);
    }

    // Chat with newer messages have bigger priority.
    @Test
    void testCompareWhenBothHaveMessages() {
        var chat1 = new Chat(uuid, name, members);
        var chat2 = new Chat(uuid, name, members);

        var message1 = mock(ChatMessage.class);
        given(message1.timestamp()).willReturn(new Timestamp(1));

        var message2 = mock(ChatMessage.class);
        given(message2.timestamp()).willReturn(new Timestamp(2));

        chat1.addMessage(message1);
        chat2.addMessage(message2);

        assertThat(chat1.compareTo(chat2)).isGreaterThan(0);
        assertThat(chat2.compareTo(chat1)).isLessThan(0);
    }
}
