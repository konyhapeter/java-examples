package app;

import app.controller.KafkaController.MessageAcknowledgement;
import app.services.KafkaConsumer;
import app.wikimedia.WikimediaRecentChange;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KafkaFlowIntegrationTest {

    @Container
    static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private KafkaConsumer kafkaConsumer;

    @Test
    void publishesKeyedMessageAndConsumesIt() {
        ResponseEntity<MessageAcknowledgement> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/kafka/publish?key=order-42&message=created",
                null,
                MessageAcknowledgement.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().topic()).isEqualTo("orders");
        assertThat(response.getBody().partition()).isBetween(0, 2);
        assertThat(response.getBody().offset()).isGreaterThanOrEqualTo(0);
        assertThat(response.getBody().key()).isEqualTo("order-42");

        ResponseEntity<MessageAcknowledgement> nextResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/kafka/publish?key=order-42&message=shipped",
                null,
                MessageAcknowledgement.class);

        assertThat(nextResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(nextResponse.getBody()).isNotNull();
        assertThat(nextResponse.getBody().partition()).isEqualTo(response.getBody().partition());
        assertThat(nextResponse.getBody().offset()).isGreaterThan(response.getBody().offset());

        verify(kafkaConsumer, timeout(10_000)).consume(argThat(
                (ConsumerRecord<String, String> record) ->
                        record.key().equals("order-42") && record.value().equals("created")));
        verify(kafkaConsumer, timeout(10_000)).consume(argThat(
                (ConsumerRecord<String, String> record) ->
                        record.key().equals("order-42") && record.value().equals("shipped")));
    }

    @Test
    void serializesWikimediaChangeAsJsonAndConsumesIt() {
        WikimediaRecentChange change = new WikimediaRecentChange(
                "event-123",
                "enwiki",
                "Spring_Framework",
                "edit",
                "ExampleEditor",
                Instant.parse("2026-10-25T18:00:00Z"),
                "https://en.wikipedia.org/wiki/Spring_Framework",
                987654L);

        ResponseEntity<MessageAcknowledgement> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/kafka/wikimedia",
                change,
                MessageAcknowledgement.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().topic()).isEqualTo("wiki.recent-changes");
        assertThat(response.getBody().key()).isEqualTo("enwiki:Spring_Framework");
        assertThat(response.getBody().partition()).isBetween(0, 2);
        assertThat(response.getBody().offset()).isGreaterThanOrEqualTo(0);

        verify(kafkaConsumer, timeout(10_000)).consumeWikimediaChange(argThat(
                (ConsumerRecord<String, String> record) ->
                        record.key().equals("enwiki:Spring_Framework")
                                && record.value().contains("\"eventId\":\"event-123\"")
                                && record.value().contains("\"revisionId\":987654")));
    }
}
