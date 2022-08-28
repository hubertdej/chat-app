package com.chat.client.network;

import com.chat.client.domain.Credentials;
import com.chat.client.domain.application.AuthService;

import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AuthServiceImpl implements AuthService {
    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final HttpRequestFactory httpRequestFactory = new HttpRequestFactory();

    @Override
    public CompletableFuture<Void> registerUserAsync(String username, String password) {
        var request = httpRequestFactory.createPOSTRequest("/register", Map.ofEntries(
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

    @Override
    public CompletableFuture<Credentials> loginUserAsync(String username, String password) {
        var request = httpRequestFactory.createPOSTRequest("/authenticate", Map.ofEntries(
                Map.entry("username", username),
                Map.entry("password", password)
        ));

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(
                response -> {
                    System.out.println("Response body:\n" + response.body());
                    System.out.println("Status: " + response.statusCode());

                    if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                        throw new HttpException(response.statusCode());
                    }

                    return new Credentials(username, password);
                }
        );
    }
}
