package app;

import app.dto.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class WebFluxAppTest {

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void testStreamCustomers() {
        Flux<Customer> responseBody = webTestClient.get()
                .uri("/stream")
                .accept(org.springframework.http.MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Customer.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNextCount(5) // for example, if you know it streams 5 customers
                .thenCancel()       // cancel the subscription to stop consuming the infinite stream
                .verify();
    }
}
