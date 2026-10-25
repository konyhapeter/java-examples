package app.wikimedia;

import app.services.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class WikimediaEventStreamTest {

    private final WikimediaEventStream eventStream = new WikimediaEventStream(
            WebClient.builder(),
            mock(KafkaProducer.class),
            new ObjectMapper(),
            "https://example.org/events",
            2);

    @Test
    void mapsWikimediaRecentChangeToDomainEvent() {
        WikimediaRecentChange change = eventStream.toRecentChange("""
                {
                  "meta": {
                    "id": "event-123",
                    "uri": "https://en.wikipedia.org/wiki/Spring_Framework"
                  },
                  "wiki": "enwiki",
                  "title": "Spring_Framework",
                  "type": "edit",
                  "user": "ExampleEditor",
                  "timestamp": 1792951200,
                  "revision": {"new": 987654}
                }
                """);

        assertThat(change.eventId()).isEqualTo("event-123");
        assertThat(change.wiki()).isEqualTo("enwiki");
        assertThat(change.title()).isEqualTo("Spring_Framework");
        assertThat(change.changeType()).isEqualTo("edit");
        assertThat(change.user()).isEqualTo("ExampleEditor");
        assertThat(change.occurredAt().getEpochSecond()).isEqualTo(1792951200);
        assertThat(change.revisionId()).isEqualTo(987654L);
    }

    @Test
    void rejectsWikimediaEventsWithoutRequiredFields() {
        assertThatThrownBy(() -> eventStream.toRecentChange("""
                {"meta":{"id":"event-123"},"wiki":"enwiki","type":"edit","timestamp":1}
                """))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title");
    }
}
