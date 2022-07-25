package com.chat.client.fake;

import com.chat.client.domain.Account;
import com.chat.client.domain.User;
import com.chat.client.domain.application.AuthService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FakeAuthService implements AuthService {
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
                    registeredAccounts.add(new Account(new User(username), password));

                    System.out.println("Registered accounts:");
                    for (var account : registeredAccounts) {
                        System.out.println(account.getUsername() + " " + account.getPassword());
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

                    var account = new Account(new User(username), password);

                    if (registeredAccounts.stream().noneMatch(a -> a.getUsername().equals(username) && a.getPassword().equals(password))) {
                        throw new RuntimeException("No such account");
                    }

                    return account;
                }
        );
    }
}
