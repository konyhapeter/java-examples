package app.wikimedia;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class WikimediaStreamControllerTest {

    @Test
    void proxiesRawServerSentEventsAndForwardsLastEventId() {
        String sse = ": heartbeat\nid: event-123\ndata: {\"title\":\"Example\"}\n\n";
        AtomicReference<String> acceptHeader = new AtomicReference<>();
        AtomicReference<String> lastEventIdHeader = new AtomicReference<>();
        WebClient.Builder builder = WebClient.builder().exchangeFunction(request -> {
            acceptHeader.set(request.headers().getFirst(HttpHeaders.ACCEPT));
            lastEventIdHeader.set(request.headers().getFirst("Last-Event-ID"));
            DataBuffer body = new DefaultDataBufferFactory()
                    .wrap(sse.getBytes(StandardCharsets.UTF_8));
            return Mono.just(ClientResponse.create(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)
                    .body(Flux.just(body))
                    .build());
        });
        WikimediaStreamController controller =
                new WikimediaStreamController(builder, "https://example.org/events");

        DataBuffer response = Objects.requireNonNull(
                DataBufferUtils.join(controller.recentChanges("previous-event")).block());
        byte[] bytes = new byte[response.readableByteCount()];
        response.read(bytes);
        DataBufferUtils.release(response);

        assertThat(new String(bytes, StandardCharsets.UTF_8)).isEqualTo(sse);
        assertThat(acceptHeader.get()).isEqualTo(MediaType.TEXT_EVENT_STREAM_VALUE);
        assertThat(lastEventIdHeader.get()).isEqualTo("previous-event");
    }
}
