package com.chat.client.network;

import com.chat.client.domain.Chat;
import com.chat.client.domain.Credentials;
import com.chat.client.domain.User;
import com.chat.client.domain.application.ChatsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ChatsServiceImpl implements ChatsService {
    private final Credentials credentials;

    public ChatsServiceImpl(Credentials credentials) {
        this.credentials = credentials;
    }

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final HttpRequestFactory httpRequestFactory = new HttpRequestFactory();

    @Override
    public CompletableFuture<Chat> createChatAsync(String name, List<User> recipients) {
        var request = httpRequestFactory.createPOSTRequest("/add-conversation", Map.ofEntries(
                Map.entry("name", name),
                Map.entry("members", recipients.stream().map(User::name).toList())
        ));

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(
                response -> {
                    System.out.println("Response body:\n" + response.body());
                    System.out.println("Status: " + response.statusCode());

                    if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                        throw new HttpException(response.statusCode());
                    }

                    var uuid = UUID.fromString(response.body());
                    return new Chat(uuid, name, recipients);
                }
        );
    }

    @Override
    public CompletableFuture<Chat> getChatDetails(UUID chatUUID) {
        var request = httpRequestFactory.createPOSTRequest("/get-conversation", Map.ofEntries(
                Map.entry("username", credentials.username()),
                Map.entry("password", credentials.password()),
                Map.entry("conversationId", chatUUID)
        ));

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(
                response -> {
                    System.out.println("Response:\n" + response);
                    System.out.println("Response body:\n" + response.body());
                    System.out.println("Status: " + response.statusCode());

                    record Message(String from, UUID to, String content, Timestamp timestamp) {}

                    record ChatDetails(UUID conversationId, String name, String[] members, Message[] messages) {}

                    try {
                        var chatDetails = new ObjectMapper().readValue(response.body(), ChatDetails.class);
                        assert(chatUUID.equals(chatDetails.conversationId));
                        return new Chat(chatUUID, chatDetails.name, Arrays.stream(chatDetails.members).map(User::new).toList());
                    } catch (JsonProcessingException e) {
                        throw new JsonException(e);
                    }
                }
        );
    }
}
