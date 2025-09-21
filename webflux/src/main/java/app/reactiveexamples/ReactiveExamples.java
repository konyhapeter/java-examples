package app.reactiveexamples;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveExamples {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4);
        Mono<Integer> mono = Mono.just(1);

        // Subscribe and consume
        mono.subscribe(System.out::println);

        // Empty Mono (completes without emitting anything)
        Mono.empty().subscribe(
                value -> System.out.println("Value: " + value),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completed without value")
        );

        // Mono with error
        Mono.error(new RuntimeException("Something went wrong"))
                .subscribe(
                        value -> System.out.println("Value: " + value),
                        error -> System.err.println("Caught: " + error)
                );

        System.out.println("\nFlux comes\n");
        // Subscribe and consume
        flux.subscribe(System.out::println);

        // Flux from iterable
        Flux<Integer> numbers = Flux.fromIterable(java.util.List.of(1, 2, 3, 4));
        numbers.subscribe(n -> System.out.println("Number: " + n));

        // Flux with error
        Flux<String> fluxWithError = Flux.just("X", "Y")
                .concatWith(Flux.error(new RuntimeException("Boom!")))
                .concatWith(Flux.just("Z")); // won't execute after error

        fluxWithError.subscribe(
                System.out::println,
                error -> System.err.println("Caught: " + error),
                () -> System.out.println("Completed")
        );

        // Infinite stream with delay
        Flux<Long> intervalFlux = Flux.interval(java.time.Duration.ofSeconds(1))
                .take(50); // take only 5 items to avoid infinite loop

        intervalFlux.subscribe(i -> System.out.println("Tick: " + i));

        // Sleep main thread so we can see async ticks
        try { Thread.sleep(6000); } catch (InterruptedException e) {}
    }
}