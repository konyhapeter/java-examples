package app.java11;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    static void main() throws IOException, InterruptedException {
        var message = "Hello, Java 11"; // inferred as String
        var number = 42;                // inferred as int

        System.out.println(message + " - " + number);



        String text = "   Java 11 is here!   ";

        System.out.println("".isBlank());         // true
        System.out.println(" Java ".strip());     // "Java"
        System.out.println("Java".repeat(3)); // "JavaJavaJava"
        text.lines().forEach(System.out::println);

        System.out.println();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/get"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());
    }
}
