package app.java25;

import java.util.concurrent.StructuredTaskScope;

final class StructuredConcurrencyExample {

    /**
     * Runs two subtasks in one structured scope and prints their results.
     *
     * @throws InterruptedException if the current thread is interrupted while joining
     */
    static void run() throws InterruptedException {
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.allSuccessfulOrThrow())) {
            scope.fork(() -> "first");
            scope.fork(() -> "second");
            var results = scope.join();
            System.out.println("Structured concurrency: " + results.map(StructuredTaskScope.Subtask::get).toList());
        }
    }
}
