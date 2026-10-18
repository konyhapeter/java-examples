package app.concurrency.reactive;

import app.concurrency.IoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
class WebFluxIoController {

    @GetMapping("/io")
    Mono<IoResponse> simulateIo(@RequestParam(defaultValue = "100") int delayMs) {
        validateDelay(delayMs);
        return Mono.delay(Duration.ofMillis(delayMs))
                .map(ignored -> new IoResponse(
                        "webflux", delayMs, Thread.currentThread().isVirtual()));
    }

    private static void validateDelay(int delayMs) {
        if (delayMs < 0 || delayMs > 1_000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "delayMs must be between 0 and 1000");
        }
    }
}
