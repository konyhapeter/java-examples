package app;

import app.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class IntegrationTest {

    private WebSocketGraphQlClient client;

    @LocalServerPort
    private int port;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup(){
        var url = "ws://localhost:" + port + "/ws/graphql";
        log.debug("websocket connection url: {}", url);
        this.client = WebSocketGraphQlClient.builder(url, new ReactorNettyWebSocketClient()).build();
        this.client.start();
    }

    @AfterEach
    public void tearDown(){
        client.stop();
    }

    @Test
    public void testMessageSubscription() {
        String subscriptionQuery = "subscription bookAdded{\n" +
                "    bookAdded {\n" +
                "        id\n" +
                "        text\n" +
                "    }\n" +
                "}";
        Flux<Message> result = this.client.document(subscriptionQuery)
                .executeSubscription()
                .map(response -> objectMapper.convertValue(
                                response.<Map<String, Object>>getData().get("bookAdded"),
                                Message.class));

        var verify = StepVerifier.create(result) // take first 3 messages
                .consumeNextWith(message -> message.text().startsWith("Message number0"))
                .consumeNextWith(message -> message.text().startsWith("Message number1"))
                .consumeNextWith(message -> message.text().startsWith("Message number2"))
                .thenCancel().verifyLater();

        verify.verify();
    }
}
