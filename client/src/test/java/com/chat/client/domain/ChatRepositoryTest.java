package com.chat.client.domain;

import com.chat.client.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class ChatRepositoryTest extends BaseTestCase {
    private ChatsRepository chatsRepository;

    @BeforeEach
    void setup() {
        chatsRepository = new ChatsRepository();
    }

    @Test
    void testCreate() {
        assertThat(chatsRepository.getChats()).isEmpty();
    }

    @Test
    void testAddNewChats() {
        var chat1 = mock(Chat.class);
        var chat2 = mock(Chat.class);
        chatsRepository.addChat(chat1);
        chatsRepository.addChat(chat2);
        assertThat(chatsRepository.getChats()).containsExactly(chat1, chat2);
    }

    @Test
    void testGetByUUID() {
        var uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000000");
        var uuid2 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        var chat1 = new Chat(uuid1, "name1", List.of());
        var chat2 = new Chat(uuid2, "name2", List.of());

        chatsRepository.addChat(chat1);
        chatsRepository.addChat(chat2);

        var result = chatsRepository.getByUUID(uuid1);
        assertTrue(result.isPresent());
        assertEquals(chat1, result.get());
    }

    @Test
    void testObserver() {
        var chat1 = mock(Chat.class);
        var chat2 = mock(Chat.class);
        ChatsRepository.Observer observer = mock(ChatsRepository.Observer.class);

        chatsRepository.addObserver(observer);
        chatsRepository.addChat(chat1);
        chatsRepository.removeObserver(observer);
        chatsRepository.addChat(chat2);

        then(observer).should(times(1)).notifyUpdate(chat1);
        then(observer).shouldHaveNoMoreInteractions();
    }
}
