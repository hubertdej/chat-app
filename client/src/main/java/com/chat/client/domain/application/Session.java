package com.chat.client.domain.application;

import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.Credentials;
import com.chat.client.domain.User;
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
