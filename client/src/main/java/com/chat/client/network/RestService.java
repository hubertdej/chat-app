package com.chat.client.network;

import com.chat.client.domain.Account;
import com.chat.client.domain.Chat;
import com.chat.client.domain.User;
import com.chat.client.domain.application.AuthService;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.UsersService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RestService implements AuthService, UsersService, ChatsService {
    private static final String ADDRESS = "http://localhost:8080";
    private final HttpClient httpClient = HttpClient.newBuilder().build();

    private HttpRequest buildPOSTRequest(String pathname, Map<String, ?> map) {
        var json = new ObjectMapper().valueToTree(map);

        System.out.println("Building POST request for pathname: " + pathname);
        System.out.println(json.toPrettyString());

        return HttpRequest.newBuilder(URI.create(ADDRESS + pathname))
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .header("Content-Type", "application/json")
                .build();
    }

    private HttpRequest buildGETRequest(String pathname) {
        System.out.println("Building GET request for pathname: " + pathname);

        return HttpRequest.newBuilder(URI.create(ADDRESS + pathname)).GET().build();
    }

    @Override
    public CompletableFuture<Void> registerUserAsync(String username, String password) {
        var request = buildPOSTRequest("/register", Map.ofEntries(
                Map.entry("username", username),
                Map.entry("password", password)
        ));

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(
                response -> {
                    System.out.println("Response body:\n" + response.body());
                    System.out.println("Status: " + response.statusCode());

                    if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                        throw new HttpException(response.statusCode());
                    }
                }
        );
    }


    // TODO: Don't do that!
    private Account account;

    // TODO: Replace dummy implementation!
    @Override
    public CompletableFuture<Account> loginUserAsync(String username, String password) {
        account = new Account(new User(username), password);
        return CompletableFuture.completedFuture(account);
    }

    @Override
    public CompletableFuture<List<User>> getUsersAsync() {
        var request = buildGETRequest("/list-users");

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(
                response -> {
                    System.out.println("Response:\n" + response);
                    System.out.println("Response body:\n" + response.body());
                    System.out.println("Status: " + response.statusCode());

                    if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                        throw new HttpException(response.statusCode());
                    }

                    try {
                        var usernames = new ObjectMapper().readValue(response.body(), String[].class);
                        return Arrays.stream(usernames).map(User::new).toList();
                    } catch (JsonProcessingException e) {
                        throw new JsonException(e);
                    }
                }
        );
    }

    @Override
    public CompletableFuture<Chat> createChatAsync(String name, List<User> recipients) {
        var request = buildPOSTRequest("/add-conversation", Map.ofEntries(
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

    // TODO: Extract to a class with injected credentials.
    @Override
    public CompletableFuture<Chat> getChatDetails(UUID chatUUID) {
        var request = buildPOSTRequest("/get-conversation", Map.ofEntries(
                Map.entry("username", account.getUsername()),
                Map.entry("password", account.getPassword()),
                Map.entry("conversationId", chatUUID)
        ));

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(
                response -> {
                    System.out.println("Response:\n" + response);
                    System.out.println("Response body:\n" + response.body());
                    System.out.println("Status: " + response.statusCode());

                    record Message(String from, UUID to, String content, Timestamp timestamp) {
                    }

                    record ChatDetails(UUID conversationId, String name, String[] members, Message[] messages) {
                    }

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
