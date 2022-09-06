package com.chat.client.domain;

import com.chat.client.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.UUID;

import static com.chat.client.domain.ChatMessageAssert.assertThat;

public class MessageFactoryTest extends BaseTestCase {
    @Test
    void testCreateSentByLocalUser() {
        var uuid = UUID.randomUUID();
        var messageContent = "message-content";
        var localUserUsername = "some-username-of-local-user";
        var timestamp = new Timestamp(0);

        var localUser = new User(localUserUsername);
        var factory = new MessageFactory(localUser);

        var message = factory.createMessage(uuid, messageContent, localUserUsername, timestamp);

        assertThat(message).hasData(uuid, messageContent, localUser, timestamp, true);
    }

    @Test
    void testCreateSentByNonlocalUser() {
        var uuid = UUID.randomUUID();
        var messageContent = "message-content";
        var localUserUsername = "some-username-of-local-user";
        var timestamp = new Timestamp(0);

        var localUser = new User(localUserUsername);
        var factory = new MessageFactory(localUser);
        var otherUsername = "other-username";
        var otherUser = new User(otherUsername);

        var message = factory.createMessage(uuid, messageContent, otherUsername, timestamp);

        assertThat(message).hasData(uuid, messageContent, otherUser, timestamp, false);
    }
}
