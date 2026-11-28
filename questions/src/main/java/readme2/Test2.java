void main() throws ExecutionException, InterruptedException {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        Future<String> response = executor.submit(() -> fetchFromService());
        System.out.println(Thread.currentThread().threadId() + " is waiting for the response...");
        System.out.println(response.get());
    }

    System.out.println(describe(42));

    // text blocks
    String json = """
        {
          "name": "Ada",
          "active": true
        }
        """;

    SequencedCollection<String> names = new ArrayList<>(List.of("Ada", "Linus"));
    names.addFirst("Grace");
    System.out.println(names.getLast()); // Linus
    System.out.println(names.reversed()); // revers
}

private String fetchFromService() throws InterruptedException {
    System.out.println(Thread.currentThread().threadId()+ " inside virtual thread");
    Thread.sleep(2000); // Simulate a delay in fetching data
    return "Hello from the service!";
}

//pattern matching
static String describe(Object value) {
    return switch (value) {
        case null -> "No value";
        case Integer i when i > 0 -> "Positive integer: " + i;
        case Integer i -> "Other integer: " + i;
        case String s -> "Text: " + s;
        default -> "Something else";
    };
}