package com.chat.client.fake;

import com.chat.client.domain.Account;
import com.chat.client.domain.AccountRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FakeRestClient implements AccountRepository {
    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final URI uri = URI.create("https://postman-echo.com/post");

    private HttpRequest buildPOSTRequest(String body) {
        return HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(body)).build();
    }

    private final Set<String> registeredUsernames = new HashSet<>();
    private final List<Account> registeredAccounts = new ArrayList<>();

    @Override
    public CompletableFuture<Void> registerUserAsync(String username, String password) {
        HttpRequest request = buildPOSTRequest(username + " " + password);

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(
                stringHttpResponse -> {
                    System.out.println("Echoed registration request:\n" + stringHttpResponse.body());

                    if (registeredUsernames.contains(username)) {
                        throw new RuntimeException("Account with this username already exists");
                    }
                    registeredUsernames.add(username);
                    registeredAccounts.add(new Account(username, password));

                    System.out.println("Registered accounts:");
                    for (var account : registeredAccounts) {
                        System.out.println(account.username() + " " + account.password());
                    }
                }
        );
    }

    @Override
    public CompletableFuture<Account> loginUserAsync(String username, String password) {
        HttpRequest request = buildPOSTRequest(username + " " + password);

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(
                stringHttpResponse -> {
                    System.out.println("Echoed login request:\n" + stringHttpResponse.body());

                    var account = new Account(username, password);
                    if (!registeredAccounts.contains(account)) {
                        throw new RuntimeException("No such account");
                    }
                    return account;
                }
        );
    }
}
