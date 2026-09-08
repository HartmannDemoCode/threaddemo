package dk.ek;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpFetcher {
    static void main() {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }
    public FetchResult fetch(HttpClient client, String url){
        // 1. Build an HttpRequest for the URL.
        // Create a request
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check the status code and print the response
        if (response.statusCode() != 200) {
            throw new RuntimeException("GET request failed. Status code: " + response.statusCode());
        }
        // 2. Add a request timeout.
        // 3. Send the request.
        // 4. Calculate the request duration.
        // 5. Return a FetchResult.
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
