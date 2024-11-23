package com.raphaelprojetos.Sentinel.alerts;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

//Todo: Integrar o client ao Jframe

@Component
public class ApiClient {

    private static final String BASE_URL = "http://26.92.48.121/alertas";

    public static String FetchAlerts() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();

    }
}
