package app.reactiveexamples;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class AsyncFluxTransformations {
    public static void main(String[] args) throws InterruptedException {
        // it would be infinite without .take(10)
        Flux<Long> ticks = Flux.interval(Duration.ofSeconds(1)).take(10);
        // Transform the stream
        Flux<String> stream = ticks
                .map(n -> n + 1)                  // start from 1 instead of 0
                .filter(n -> n % 2 == 0)          // keep even numbers only
                .map(n -> "Tick #" + n);          // transform to string

        // Subscribe to consume values
        stream.subscribe(System.out::println);

        // Keep the program alive for 10 seconds to observe
        //In Project Reactor, you don’t need to rely on Thread.sleep() in tests.
        // Instead, you use StepVerifier from the Reactor Test library to verify reactive streams,
        // including async ones like Flux.interval.
        Thread.sleep(10000);

        Flux<Integer> numbers = Flux.range(1, 5);

        numbers.flatMap(n ->
                        Flux.just(n)
                                .delayElements(Duration.ofMillis(500)) // async delay
                                .map(x -> "Processed " + x)
                )
                .subscribe(System.out::println);

        Thread.sleep(4000); // keep JVM alive to see output

        Flux<String> messages = Flux.just("A", "B", "C", "D");
        Flux<Long> timer = Flux.interval(Duration.ofSeconds(1));

        // zip combines timer + messages, emitting one per second
        Flux.zip(timer, messages, (t, msg) -> "At " + t + "s: " + msg)
                .subscribe(System.out::println);

        Thread.sleep(5000); // keep JVM alive
    }
}
