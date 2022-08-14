package com.chat.client.network;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Map;

public class HttpRequestFactory {
    private static final String ADDRESS = "http://localhost:8080";

    public HttpRequest createPOSTRequest(String pathname, Map<String, ?> map) {
        var json = new ObjectMapper().valueToTree(map);

        System.out.println("Building POST request for pathname: " + pathname);
        System.out.println(json.toPrettyString());

        return HttpRequest.newBuilder(URI.create(ADDRESS + pathname))
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .header("Content-Type", "application/json")
                .build();
    }

    public HttpRequest createGETRequest(String pathname) {
        System.out.println("Building GET request for pathname: " + pathname);

        return HttpRequest.newBuilder(URI.create(ADDRESS + pathname)).GET().build();
    }
}
