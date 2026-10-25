package app.services;

import app.wikimedia.WikimediaRecentChange;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaProducerTest {

    @Test
    void publishesWikimediaEventAsJsonUnderPageKey() {
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        CompletableFuture<SendResult<String, String>> acknowledgement =
                CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(eq("wiki.recent-changes"),
                eq("enwiki:Spring_Framework"), anyString()))
                .thenReturn(acknowledgement);
        KafkaProducer producer = new KafkaProducer(
                kafkaTemplate,
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS),
                "orders",
                "wiki.recent-changes");
        WikimediaRecentChange change = new WikimediaRecentChange(
                "event-123", "enwiki", "Spring_Framework", "edit", "ExampleEditor",
                Instant.parse("2026-10-25T18:00:00Z"),
                "https://en.wikipedia.org/wiki/Spring_Framework", 987654L);

        CompletableFuture<SendResult<String, String>> result =
                producer.publishWikimediaChange(change);

        assertThat(result).isSameAs(acknowledgement);
        verify(kafkaTemplate).send(eq("wiki.recent-changes"),
                eq("enwiki:Spring_Framework"),
                eq("{\"eventId\":\"event-123\",\"wiki\":\"enwiki\","
                        + "\"title\":\"Spring_Framework\",\"changeType\":\"edit\","
                        + "\"user\":\"ExampleEditor\",\"occurredAt\":"
                        + "\"2026-10-25T18:00:00Z\",\"pageUrl\":"
                        + "\"https://en.wikipedia.org/wiki/Spring_Framework\","
                        + "\"revisionId\":987654}"));
    }
}
