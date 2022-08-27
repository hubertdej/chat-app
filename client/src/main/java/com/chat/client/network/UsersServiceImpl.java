package com.chat.client.network;

import com.chat.client.domain.Credentials;
import com.chat.client.domain.User;
import com.chat.client.domain.application.UsersService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UsersServiceImpl implements UsersService {
    private final Credentials credentials;

    public UsersServiceImpl(Credentials credentials) {
        this.credentials = credentials;
    }

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final HttpRequestFactory httpRequestFactory = new HttpRequestFactory();

    private CompletableFuture<List<User>> getAllUsersAsync() {
        var request = httpRequestFactory.createGETRequest("/list-users");

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
                        return Arrays.stream(usernames).map(User::new).collect(Collectors.toList());
                    } catch (JsonProcessingException e) {
                        throw new JsonException(e);
                    }
                }
        );
    }


    @Override
    public CompletableFuture<List<User>> getUsersAsync() {
        return getAllUsersAsync().thenApply(users -> {
            users.remove(new User(credentials.username()));
            return users;
        });
    }
}
