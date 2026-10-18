package app.concurrency;

import app.concurrency.reactive.ReactiveEndpointConfiguration;
import app.concurrency.virtualthreads.VirtualThreadEndpointConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrencyComparisonIntegrationTest {

    private static final int REQUEST_COUNT = 80;
    private static final int SIMULATED_IO_DELAY_MS = 150;

    private static ConfigurableApplicationContext virtualThreadsContext;
    private static ConfigurableApplicationContext webFluxContext;
    private static int virtualThreadsPort;
    private static int webFluxPort;

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @BeforeAll
    static void startServers() {
        virtualThreadsContext = new SpringApplicationBuilder(
                VirtualThreadEndpointConfiguration.class)
                .web(WebApplicationType.SERVLET)
                .properties(
                        "server.address=127.0.0.1",
                        "server.port=0",
                        "spring.threads.virtual.enabled=true",
                        "logging.level.root=WARN")
                .run();
        virtualThreadsPort =
                ((ServletWebServerApplicationContext) virtualThreadsContext)
                        .getWebServer().getPort();

        webFluxContext = new SpringApplicationBuilder(ReactiveEndpointConfiguration.class)
                .web(WebApplicationType.REACTIVE)
                .properties(
                        "server.address=127.0.0.1",
                        "server.port=0",
                        "logging.level.root=WARN")
                .run();
        webFluxPort =
                ((ReactiveWebServerApplicationContext) webFluxContext)
                        .getWebServer().getPort();
    }

    @AfterAll
    static void stopServers() {
        httpClient.close();
        if (webFluxContext != null) {
            webFluxContext.close();
        }
        if (virtualThreadsContext != null) {
            virtualThreadsContext.close();
        }
    }

    @Test
    void bothImplementationsExposeEquivalentIoEndpoints() throws Exception {
        HttpResponse<String> virtualResponse = get(virtualThreadsPort, 10);
        HttpResponse<String> webFluxResponse = get(webFluxPort, 10);

        assertThat(virtualResponse.statusCode()).isEqualTo(200);
        assertThat(virtualResponse.body())
                .contains("\"implementation\":\"virtual-threads\"")
                .contains("\"delayMs\":10")
                .contains("\"virtualThread\":true");

        assertThat(webFluxResponse.statusCode()).isEqualTo(200);
        assertThat(webFluxResponse.body())
                .contains("\"implementation\":\"webflux\"")
                .contains("\"delayMs\":10")
                .contains("\"virtualThread\":false");

        assertThat(get(virtualThreadsPort, 1_001).statusCode()).isEqualTo(400);
        assertThat(get(webFluxPort, 1_001).statusCode()).isEqualTo(400);
    }

    @Test
    void comparesThroughputAndLatencyUnderConcurrentIoLoad() throws Exception {
        warmUp(virtualThreadsPort);
        warmUp(webFluxPort);

        List<Measurement> measurements = new ArrayList<>();
        for (int round = 0; round < 3; round++) {
            int firstPort = round % 2 == 0 ? virtualThreadsPort : webFluxPort;
            int secondPort = round % 2 == 0 ? webFluxPort : virtualThreadsPort;
            String firstName = round % 2 == 0 ? "virtual-threads" : "webflux";
            String secondName = round % 2 == 0 ? "webflux" : "virtual-threads";
            measurements.add(measure(firstName, firstPort, round + 1));
            measurements.add(measure(secondName, secondPort, round + 1));
        }

        System.out.println("Concurrent I/O comparison (80 requests, 150 ms simulated wait)");
        measurements.forEach(measurement -> System.out.printf(
                "%s round %d: throughput=%.1f req/s, p50=%d ms, p95=%d ms, p99=%d ms%n",
                measurement.implementation(),
                measurement.round(),
                measurement.requestsPerSecond(),
                measurement.p50Millis(),
                measurement.p95Millis(),
                measurement.p99Millis()));

        assertThat(measurements).hasSize(6);
        assertThat(measurements).allSatisfy(measurement -> {
            assertThat(measurement.requestsPerSecond()).isPositive();
            assertThat(measurement.latenciesMillis()).hasSize(REQUEST_COUNT);
            assertThat(measurement.p50Millis()).isPositive();
        });
    }

    private void warmUp(int port) throws Exception {
        for (int i = 0; i < 12; i++) {
            assertThat(get(port, 5).statusCode()).isEqualTo(200);
        }
    }

    private Measurement measure(String implementation, int port, int round) throws Exception {
        List<Long> latencies = new ArrayList<>(REQUEST_COUNT);
        List<CompletableFuture<Long>> requests = new ArrayList<>(REQUEST_COUNT);
        long startNanos = System.nanoTime();

        for (int i = 0; i < REQUEST_COUNT; i++) {
            long requestStart = System.nanoTime();
            HttpRequest request = HttpRequest.newBuilder(uri(port, SIMULATED_IO_DELAY_MS))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
            requests.add(httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() != 200) {
                            throw new IllegalStateException(
                                    implementation + " returned HTTP " + response.statusCode());
                        }
                        return Duration.ofNanos(System.nanoTime() - requestStart).toMillis();
                    }));
        }

        for (CompletableFuture<Long> request : requests) {
            latencies.add(request.get());
        }

        long elapsedNanos = System.nanoTime() - startNanos;
        latencies.sort(Comparator.naturalOrder());
        double requestsPerSecond = REQUEST_COUNT / (elapsedNanos / 1_000_000_000.0);
        return new Measurement(
                implementation,
                round,
                requestsPerSecond,
                percentile(latencies, 0.50),
                percentile(latencies, 0.95),
                percentile(latencies, 0.99),
                List.copyOf(latencies));
    }

    private static long percentile(List<Long> sortedLatencies, double percentile) {
        int index = (int) Math.ceil(percentile * sortedLatencies.size()) - 1;
        return sortedLatencies.get(Math.max(index, 0));
    }

    private HttpResponse<String> get(int port, int delayMs) throws Exception {
        return httpClient.send(
                HttpRequest.newBuilder(uri(port, delayMs)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private static URI uri(int port, int delayMs) {
        return URI.create("http://127.0.0.1:" + port + "/io?delayMs=" + delayMs);
    }

    private record Measurement(
            String implementation,
            int round,
            double requestsPerSecond,
            long p50Millis,
            long p95Millis,
            long p99Millis,
            List<Long> latenciesMillis) {
    }
}
