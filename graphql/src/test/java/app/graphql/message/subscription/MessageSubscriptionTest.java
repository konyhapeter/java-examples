package app.graphql.message.subscription;

import app.controller.BookController;
import app.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@GraphQlTest(controllers = BookController.class)
public class MessageSubscriptionTest {

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    public void testMessageSubscription() {
        String subscriptionQuery = "subscription bookAdded{\n" +
                "    bookAdded {\n" +
                "        id\n" +
                "        text\n" +
                "    }\n" +
                "}";

        Flux<Message> subscriptionMessageFlux = graphQlTester
                .document(subscriptionQuery)
                .executeSubscription()
                .toFlux("bookAdded", Message.class);

        StepVerifier.create(subscriptionMessageFlux.take(3)) // take first 3 messages
                .expectNextMatches(message -> message.text().startsWith("Message number0"))
                .expectNextMatches(message -> message.text().startsWith("Message number1"))
                .expectNextMatches(message -> message.text().startsWith("Message number2"))
                .verifyComplete();

        //https://github.com/hantsy/spring-graphql-sample/blob/master/spring-graphql-webflux/src/test/java/com/example/demo/IntegrationTests.java
    }
}
