package app.reactiveexamples;

import reactor.core.publisher.Mono;

public class MonoTransformations {
    public static void main(String[] args) {
        // Start with a Mono
        Mono<String> mono = Mono.just("hello");

        // Transform with map (synchronous transformation)
        mono.map(String::toUpperCase)
                .subscribe(v -> System.out.println("Uppercase: " + v));

        // flatMap (asynchronous, returns another Mono)
        mono.flatMap(v -> Mono.just(v + " world"))
                .subscribe(v -> System.out.println("FlatMapped: " + v));

        // filter (keep only if condition matches)
        mono.filter(v -> v.startsWith("h"))
                .subscribe(v -> System.out.println("Filtered: " + v));

        // filter that results in empty
        mono.filter(v -> v.startsWith("x"))
                .subscribe(
                        v -> System.out.println("Should not print: " + v),
                        err -> System.err.println("Error: " + err),
                        () -> System.out.println("Completed empty Mono")
                );
    }
}
