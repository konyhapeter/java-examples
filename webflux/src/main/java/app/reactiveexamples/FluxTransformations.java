package app.reactiveexamples;

import reactor.core.publisher.Flux;

public class FluxTransformations {
    public static void main(String[] args) {
        Flux<Integer> numbers = Flux.range(1, 3); // 1..3

        // map (square each number)
        numbers.map(n -> n * n)
                .subscribe(v -> System.out.println("Square: " + v));

        // filter (even numbers only)
        numbers.filter(n -> n % 2 == 0)
                .subscribe(v -> System.out.println("Even: " + v));

        // flatMap (async transform, e.g., double and wrap in Flux)
        numbers.flatMap(n -> Flux.just(n, n * 10))
                .subscribe(v -> System.out.println("FlatMapped: " + v));

        // reduce (sum all numbers)
        numbers.reduce(0, Integer::sum)
                .subscribe(sum -> System.out.println("Sum: " + sum));

        // collectList (turn Flux into Mono<List<T>>)
        numbers.collectList()
                .subscribe(list -> System.out.println("As List: " + list));

        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E");

        // merge (interleaves, async)
        Flux.merge(flux1, flux2)
                .subscribe(v -> System.out.println("Merged: " + v));

        // concat (sequential)
        Flux.concat(flux1, flux2)
                .subscribe(v -> System.out.println("Concatenated: " + v));

        // zip (combine elements pairwise)
        Flux.zip(flux1, flux2, (a, b) -> a + b)
                .subscribe(v -> System.out.println("Zipped: " + v));
    }
}
