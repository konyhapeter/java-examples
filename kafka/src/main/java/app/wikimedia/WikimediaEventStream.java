package app.wikimedia;

import app.services.KafkaProducer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Component
@ConditionalOnProperty(prefix = "app.wikimedia", name = "enabled", havingValue = "true")
public class WikimediaEventStream {

    private static final Logger log = LoggerFactory.getLogger(WikimediaEventStream.class);
    private static final ParameterizedTypeReference<ServerSentEvent<String>> EVENT_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final WebClient webClient;
    private final String eventStreamUrl;
    private final long retryDelaySeconds;
    private final KafkaProducer producer;
    private final ObjectMapper objectMapper;
    private Disposable subscription;

    public WikimediaEventStream(
            WebClient.Builder webClientBuilder,
            KafkaProducer producer,
            ObjectMapper objectMapper,
            @Value("${app.wikimedia.event-stream-url}") String eventStreamUrl,
            @Value("${app.wikimedia.retry-delay-seconds}") long retryDelaySeconds) {
        this.webClient = webClientBuilder.build();
        this.producer = producer;
        this.objectMapper = objectMapper;
        this.eventStreamUrl = eventStreamUrl;
        this.retryDelaySeconds = retryDelaySeconds;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        subscription = connect()
                .doOnError(exception -> log.error(
                        "Wikimedia EventStreams connection failed", exception))
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(retryDelaySeconds))
                        .maxBackoff(Duration.ofSeconds(30))
                        .doBeforeRetry(signal -> log.warn(
                                "Reconnecting to Wikimedia EventStreams after failure: {}",
                                signal.failure().toString())))
                .repeatWhen(completed -> completed
                        .delayElements(Duration.ofSeconds(retryDelaySeconds))
                        .doOnNext(ignored -> log.warn(
                                "Wikimedia EventStreams closed; reconnecting")))
                .subscribe();
    }

    private Flux<Void> connect() {
        return webClient.get()
                .uri(eventStreamUrl)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(EVENT_TYPE)
                .map(ServerSentEvent::data)
                .filter(Objects::nonNull)
                .map(this::toRecentChange)
                .flatMap(this::publish);
    }

    private Mono<Void> publish(WikimediaRecentChange change) {
        return Mono.fromFuture(producer.publishWikimediaChange(change))
                .doOnSuccess(result -> log.debug("Published Wikimedia change eventId={} "
                                + "partition={} offset={}",
                        change.eventId(), result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()))
                .then();
    }

    WikimediaRecentChange toRecentChange(String json) {
        try {
            JsonNode event = objectMapper.readTree(json);
            JsonNode metadata = event.path("meta");
            String eventId = requiredText(metadata, "id");
            String wiki = requiredText(event, "wiki");
            String title = requiredText(event, "title");
            String changeType = requiredText(event, "type");
            long timestamp = event.path("timestamp").asLong(-1);
            if (timestamp < 0) {
                throw new IllegalArgumentException("Missing or invalid Wikimedia timestamp");
            }

            JsonNode newRevision = event.path("revision").path("new");
            Long revisionId = newRevision.isNumber() ? newRevision.longValue() : null;
            return new WikimediaRecentChange(
                    eventId,
                    wiki,
                    title,
                    changeType,
                    event.path("user").asText("unknown"),
                    Instant.ofEpochSecond(timestamp),
                    metadata.path("uri").asText(null),
                    revisionId);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not parse Wikimedia event JSON", exception);
        }
    }

    private String requiredText(JsonNode node, String field) {
        String value = node.path(field).asText(null);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required Wikimedia field: " + field);
        }
        return value;
    }

    @PreDestroy
    public void stop() {
        if (subscription != null) {
            subscription.dispose();
        }
    }
}
