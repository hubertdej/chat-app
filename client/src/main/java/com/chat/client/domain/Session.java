package com.chat.client.domain;

import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;

public record Session(
        User localUser,
        Credentials credentials,
        ChatsService chatsService,
        UsersService usersService,
        ChatsRepository chatsRepository,
        MessagingClient messagingClient
) {}
