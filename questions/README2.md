# Modern Java questions

## What are virtual threads?

Virtual threads are lightweight Java threads managed by the Java runtime rather than being permanently tied one-to-one to operating-system threads. They implement `Thread`, so most existing blocking-style code can use them while the runtime suspends a virtual thread during supported blocking operations and frees its carrier thread for other work.

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    Future<String> response = executor.submit(() -> fetchFromService());
    System.out.println(response.get());
}
```

Virtual threads became a permanent feature in Java 21.

## How are virtual threads different from platform threads?

Platform threads are typically mapped one-to-one to OS threads, which are relatively expensive resources. Virtual threads are much cheaper to create and are scheduled by the JVM onto a smaller pool of platform threads, called carrier threads. They are intended to make large numbers of concurrent, mostly-blocking tasks practical.

Virtual threads do not make CPU-bound work execute faster, and they should not be pooled: create one per task. Avoid long blocking work inside `synchronized` sections or native methods, which can pin a carrier thread in some circumstances.

## When should you use virtual threads?

Use them for applications that handle many concurrent tasks which spend much of their time waiting—for example, web requests that call databases or remote services—especially when straightforward thread-per-request code is easier to maintain than asynchronous callback chains.

Prefer platform threads or other approaches when work is CPU-bound, when a library or native call has problematic blocking behavior, or when you need explicit resource limits. Virtual threads are cheap, but connections, memory, remote service capacity, and request rates are not; apply limits such as semaphores or connection pools to those resources.

## What is Project Loom?

Project Loom is the OpenJDK project that developed lightweight threads and related concurrency features to make concurrent Java programs easier to write and scale. Its work includes virtual threads, structured concurrency, and scoped values. Virtual threads are final since Java 21; structured concurrency is still a preview API in Java 25, while scoped values became final in Java 25.

## What are structured concurrency and scoped values?

Structured concurrency treats related concurrent tasks as one unit of work, usually within a lexical scope. The parent can coordinate child tasks, handle failures, and ensure the tasks finish or are cancelled together. In Java 25, the `StructuredTaskScope` API remains a preview feature, so it requires preview features to be enabled at compile and run time.

Scoped values provide a way to share immutable context with callees and child threads for a bounded lifetime, instead of passing it through every method or relying on mutable `ThreadLocal` state. They are useful for values such as a request ID or security context. Scoped values became a final feature in Java 25.

```java
private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

static void handle(String id) {
    ScopedValue.where(REQUEST_ID, id).run(() -> processRequest());
}

static void processRequest() {
    System.out.println(REQUEST_ID.get());
}
```

## What are records, and when should you use them?

Records are concise, shallowly immutable data-carrier classes. The compiler provides a canonical constructor, accessors, and implementations of `equals()`, `hashCode()`, and `toString()` based on the record components. Use them for values such as request/response data, keys, and simple domain values when identity-by-mutable-state or class inheritance is not needed. A record does not make mutable objects held in its components immutable.

```java
record Point(int x, int y) { }

Point origin = new Point(0, 0);
System.out.println(origin.x()); // 0
```

Records became a final feature in Java 16.

## Explain sealed classes and their benefits.

Sealed classes and interfaces explicitly restrict which types may extend or implement them, using a `permits` clause. Each permitted subtype must declare itself `final`, `sealed`, or `non-sealed`. This makes a hierarchy easier to understand and allows the compiler to check exhaustive handling in constructs such as pattern-matching `switch`.

```java
sealed interface Result permits Success, Failure { }
record Success(String value) implements Result { }
record Failure(String reason) implements Result { }

static String describe(Result result) {
    return switch (result) {
        case Success s -> s.value();
        case Failure f -> "Error: " + f.reason();
    };
}
```

Sealed classes became a final feature in Java 17.

## What is pattern matching for switch?

Pattern matching for `switch` lets a case test a value's type and bind a variable of that type, avoiding a separate type check and cast. It supports guarded cases and, with sealed hierarchies, can make switches exhaustive.

```java
static String describe(Object value) {
    return switch (value) {
        case null -> "No value";
        case Integer i when i > 0 -> "Positive integer: " + i;
        case Integer i -> "Other integer: " + i;
        case String s -> "Text: " + s;
        default -> "Something else";
    };
}
```

Pattern matching for `switch` became a final feature in Java 21.

## What are text blocks?

Text blocks are multi-line string literals delimited by three double quotes. They make multi-line text—such as JSON, SQL, or HTML—more readable without manually escaping every line break and quote. Java removes incidental indentation according to the text block's closing delimiter.

```java
String json = """
        {
          "name": "Ada",
          "active": true
        }
        """;
```

Text blocks became a final feature in Java 15.

## What are sequenced collections?

Sequenced collections are collection interfaces with a defined encounter order and consistent operations for working with the first and last elements. Java 21 added `SequencedCollection`, `SequencedSet`, and `SequencedMap`, along with methods such as `getFirst()`, `getLast()`, `addFirst()`, `addLast()`, and `reversed()`.

```java
SequencedCollection<String> names = new ArrayList<>(List.of("Ada", "Linus"));
names.addFirst("Grace");
System.out.println(names.getLast()); // Linus
System.out.println(names.reversed()); // reverse-ordered view
```

These APIs provide a common way to work with ordered collections without depending on implementation-specific methods.

## What is the Foreign Function and Memory API?

The Foreign Function and Memory (FFM) API provides a supported way for Java programs to access memory outside the Java heap and call native functions, without relying on JNI for many use cases. It uses constructs such as memory segments, arenas, and layouts to model and constrain native memory access. It can be useful for native libraries, system calls, and high-performance interoperability, but incorrect native-memory handling can still cause serious errors.

```java
try (Arena arena = Arena.ofConfined()) {
    MemorySegment memory = arena.allocate(100);
    memory.set(ValueLayout.JAVA_INT, 0, 42);
    int value = memory.get(ValueLayout.JAVA_INT, 0);
}
```

The FFM API became final in Java 22.

## What are the advantages of using modern Java over Java 8?

Modern Java provides newer language features, standard APIs, and runtime improvements while continuing to support familiar object-oriented code. Depending on the target release, benefits include:

- More concise and expressive code with records, pattern matching, sealed types, and switch expressions.
- Easier concurrency with virtual threads and newer concurrency APIs.
- Modern standard APIs such as the HTTP Client and FFM API.
- Ongoing performance, security, diagnostics, and garbage-collector improvements.
- Current LTS releases with continued support and security updates.

Upgrading still requires checking library and framework compatibility, removed or encapsulated JDK internals, and deployment/runtime support. Choose an LTS release suitable for the application, test thoroughly, and migrate incrementally where practical.
