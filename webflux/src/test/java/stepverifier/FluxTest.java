package stepverifier;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxTest {

    //No need for Thread.sleep()
    @Test
    void testFiniteFlux() {
        Flux<Integer> numbers = Flux.range(1, 3); // 1, 2, 3

        StepVerifier.create(numbers)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectComplete()
                .verify();

        Flux<Long> ticks = Flux.interval(Duration.ofSeconds(1))
                .take(3); // take first 3 values

        ticks.subscribe(System.out::println);
        StepVerifier.create(ticks)
                .expectNext(0L, 1L, 2L)  // expected sequence
                .expectComplete()
                .verify();
    }
    @Test
    void testVirtualTime() {
        StepVerifier.withVirtualTime(() ->
                        Flux.interval(Duration.ofSeconds(1)).take(3)
                )
                .thenAwait(Duration.ofSeconds(3)) // fast-forward virtual time
                .expectNext(0L, 1L, 2L)
                .expectComplete()
                .verify();
    }
    @Test
    void testFluxError() {
        Flux<String> flux = Flux.just("A", "B")
                .concatWith(Flux.error(new RuntimeException("Boom!")));

        StepVerifier.create(flux)
                .expectNext("A", "B")
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Boom!"))
                .verify();
    }
}
