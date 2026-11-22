package app.wikimedia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
public class WikimediaStreamController {

    private final WebClient webClient;
    private final String eventStreamUrl;

    public WikimediaStreamController(
            WebClient.Builder webClientBuilder,
            @Value("${app.wikimedia.event-stream-url}") String eventStreamUrl) {
        this.webClient = webClientBuilder.build();
        this.eventStreamUrl = eventStreamUrl;
    }

    @GetMapping(path = "/v2/stream/recentchange", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<DataBuffer> recentChanges(
            @RequestHeader(name = "Last-Event-ID", required = false) String lastEventId) {
        WebClient.RequestHeadersSpec<?> request = webClient.get()
                .uri(eventStreamUrl)
                .accept(MediaType.TEXT_EVENT_STREAM);
        if (lastEventId != null && !lastEventId.isBlank()) {
            request.header("Last-Event-ID", lastEventId);
        }
        return request.retrieve().bodyToFlux(DataBuffer.class);
    }
}
