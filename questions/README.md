# Java interview questions

## 1. What are the main differences between Java 8, 11, 17, 21, and 25?

These are feature highlights, not an exhaustive list. Java 8 is a widely used feature baseline; Java 11, 17, 21, and 25 are Long-Term Support (LTS) releases in the current release cadence. The six-month release cadence began with Java 9.

| Version | Notable additions |
| --- | --- |
| Java 8 | Lambda expressions, functional interfaces, Streams, `Optional`, default interface methods, and the `java.time` API. |
| Java 11 | Standard HTTP Client, local-variable syntax for lambda parameters (`var`), single-file source launch, and removal of Java EE/CORBA modules from the JDK. |
| Java 17 | Sealed classes and interfaces, records, and pattern matching for `instanceof` became final; strong encapsulation of JDK internals. |
| Java 21 | Virtual threads, sequenced collections, record patterns, and pattern matching for `switch` became final. |
| Java 25 | LTS release; module import declarations, compact source files and instance `main` methods, flexible constructor bodies, and scoped values became final. |

### Java 8 feature examples

This self-contained example demonstrates lambdas, a custom functional interface, Streams, `Optional`, and the `java.time` API. It uses `Collectors.toList()`, which is available in Java 8.

```java
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Java8Features {
    @FunctionalInterface
    interface Greeting {
        String greet(String name);
    }

    static class Person {
        private final String name;
        private final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        String getName() { return name; }
        int getAge() { return age; }
    }

    public static void main(String[] args) {
        // Lambda expression implements a functional interface.
        Greeting greeting = name -> "Hello, " + name;
        System.out.println(greeting.greet("Ada"));

        List<Person> people = Arrays.asList(
                new Person("Ada", 36),
                new Person("Linus", 17),
                new Person("Grace", 40));

        // Predicate is a built-in functional interface; streams compose operations.
        Predicate<Person> isAdult = person -> person.getAge() >= 18;
        List<String> adultNames = people.stream()
                .filter(isAdult)
                .map(Person::getName)
                .collect(Collectors.toList());
        System.out.println(adultNames);

        // findFirst returns Optional, avoiding a null result when there is no match.
        Optional<String> firstAdult = people.stream()
                .filter(isAdult)
                .map(Person::getName)
                .findFirst();
        System.out.println(firstAdult.orElse("No adult found"));

        // java.time provides immutable date and time types.
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(1990, 5, 20);
        Period age = Period.between(birthday, today);
        String formattedDate = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.println("Age: " + age.getYears() + ", today: " + formattedDate);
    }
}
```

## 2. Explain the contract between `equals()` and `hashCode()`.

If two objects are equal according to `equals()`, they **must** return the same `hashCode()`. Unequal objects may share a hash code. `equals()` must be reflexive, symmetric, transitive, consistent, and false for `null`. Both methods should use the same fields that define logical equality. Violating the hash contract breaks hash-based collections.

```java
record UserId(String value) { } // generated equals() and hashCode() use value

Set<UserId> ids = new HashSet<>();
ids.add(new UserId("u-1"));
System.out.println(ids.contains(new UserId("u-1"))); // true
```

For a regular class, use `Objects.equals` and `Objects.hash` on the same equality fields.

## 3. How does `HashMap` work internally?

`HashMap` stores entries in an array of buckets. It spreads a key's hash and maps it to a bucket index (conceptually `(capacity - 1) & hash`). It checks keys in that bucket using hash and then `equals()`. Collisions are stored in the same bucket, usually as a linked list; sufficiently large buckets may become balanced trees. The table resizes when its size exceeds its capacity times the load factor (default `0.75`). Iteration order is not guaranteed. It is not thread-safe.

```java
Map<String, Integer> counts = new HashMap<>();
counts.put("pear", 2);
counts.put("pear", counts.get("pear") + 1); // replaces value for the equal key
```

## 4. What happens when two keys have the same hash code?

They collide into the same bucket. `HashMap` compares keys using `equals()` (after the hash comparison): equal keys represent the same mapping and the new value replaces the old value; unequal keys can coexist in that bucket.

```java
Map<String, Integer> map = new HashMap<>();
map.put("Aa", 1);
map.put("BB", 2); // "Aa" and "BB" have the same String hash code, but are not equal
System.out.println(map.size()); // 2
```

## 5. What is the difference between `HashMap`, `ConcurrentHashMap`, and `Hashtable`?

| Type | Thread safety | Null keys/values | Notes |
| --- | --- | --- | --- |
| `HashMap` | No | One null key; null values allowed | General-purpose, unsynchronized map. |
| `ConcurrentHashMap` | Yes | Neither allowed | Designed for concurrent access; atomic methods such as `compute` and `putIfAbsent` support compound updates. Iterators are weakly consistent. |
| `Hashtable` | Yes, synchronized methods | Neither allowed | Legacy class; usually prefer `ConcurrentHashMap` for concurrent use. |

```java
ConcurrentMap<String, Integer> counts = new ConcurrentHashMap<>();
counts.merge("pear", 1, Integer::sum); // atomic per-key update
```

## 6. Explain immutability in Java. How would you create an immutable class?

An immutable object's observable state cannot change after construction. Make the class final (or otherwise prevent mutable subclasses), fields private and final, initialize them in the constructor, expose no mutators, and defensively copy mutable inputs and outputs. A record is only shallowly immutable: referenced objects may still be mutable.

```java
public final class Team {
    private final String name;
    private final List<String> members;

    public Team(String name, List<String> members) {
        this.name = Objects.requireNonNull(name);
        this.members = List.copyOf(members);
    }

    public String name() { return name; }
    public List<String> members() { return members; } // immutable copy
}
```

## 7. What are sealed classes, records, and pattern matching?

Sealed classes restrict which classes may extend or implement them, using `permits`; permitted subtypes must declare whether they are `final`, `sealed`, or `non-sealed`. Records are concise data-carrier classes with generated accessors, constructor, `equals()`, `hashCode()`, and `toString()`. Pattern matching tests a type and binds a variable in one step; Java 21 finalized patterns in `switch` and record patterns.

```java
sealed interface Shape permits Circle, Rectangle { }
record Circle(double radius) implements Shape { }
record Rectangle(double width, double height) implements Shape { }

static double area(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
    };
}
```

## 8. Explain the difference between `final`, `finally`, and `finalize`.

`final` prevents reassignment of a variable, overriding of a method, or subclassing of a class. A `finally` block runs after `try`/`catch` in normal completion and most exception flows, typically for cleanup; try-with-resources is preferred for `AutoCloseable` resources. `finalize()` was a GC cleanup hook; it is deprecated for removal, unpredictable, and should not be used.

```java
final int limit = 10;
try (var reader = Files.newBufferedReader(path)) {
    System.out.println(reader.readLine());
} // reader is closed automatically
```

## 9. What is the difference between checked and unchecked exceptions?

Checked exceptions (subclasses of `Exception` excluding `RuntimeException`) must be caught or declared with `throws`; they commonly represent recoverable conditions a caller is expected to handle. Unchecked exceptions (`RuntimeException` and `Error` subclasses) do not have that compile-time requirement; runtime exceptions commonly indicate programming errors or invalid arguments. `Error` generally represents serious JVM or environment problems and should not normally be caught.

```java
String text = Files.readString(path); // checked IOException must be handled or declared
int number = Integer.parseInt("not a number"); // unchecked NumberFormatException
```

## 10. How does Java handle generics type erasure?

The compiler enforces generic types at compile time, then erases most type parameters from bytecode. Unbounded type variables become `Object`; bounded variables become their bound. The compiler inserts casts where needed and may generate bridge methods to preserve polymorphism. As a result, `List<String>` and `List<Integer>` have the same runtime class, and you cannot use `new T()` or `new T[... ]`.

```java
List<String> names = new ArrayList<>();
names.add("Ada");
String name = names.get(0); // compiler inserts the necessary cast after erasure
System.out.println(names.getClass() == new ArrayList<Integer>().getClass()); // true
```

## 11. What are covariant return types?

An overriding method may return a subtype of the return type declared by the overridden method. This makes APIs more specific without requiring a cast; it applies to reference types, not primitive types.

```java
class Animal { }
class Dog extends Animal { }

class Kennel {
    Animal adopt() { return new Animal(); }
}
class DogKennel extends Kennel {
    @Override Dog adopt() { return new Dog(); }
}
```

## 12. Explain the difference between `String`, `StringBuilder`, and `StringBuffer`.

`String` is immutable, so each concatenation that changes content creates a new string (though the compiler may optimize constant expressions). `StringBuilder` is a mutable character sequence and is usually the right choice for repeated appends within one thread. `StringBuffer` is similar but synchronizes its methods; that synchronization does not make a larger multi-call operation automatically atomic.

```java
StringBuilder message = new StringBuilder("Hello");
message.append(", ").append("Java");
System.out.println(message); // Hello, Java
```

## 13. How does the Java Stream API work internally?

A stream builds a pipeline from a source, intermediate operations, and a terminal operation. Intermediate operations such as `filter` and `map` are lazy; the terminal operation triggers traversal. The source is commonly traversed through a `Spliterator`, and pipeline stages process elements as needed rather than necessarily materializing each intermediate result. Streams are single-use; parallel streams may split work across threads.

```java
long count = names.stream()             // source
        .filter(name -> name.length() > 3) // lazy intermediate stage
        .map(String::toUpperCase)           // lazy intermediate stage
        .count();                           // terminal operation triggers traversal
```

## 14. What is the difference between `map()` and `flatMap()`?

`map()` transforms each element into one result, so the stream shape remains one-to-one. `flatMap()` transforms each element into a stream (or other supported container stream) and flattens those results into one stream; it is useful for one-to-many transformations or avoiding nested streams.

```java
List<List<String>> groups = List.of(List.of("a", "b"), List.of("c"));
List<String> all = groups.stream()
        .flatMap(List::stream)
        .toList(); // [a, b, c]
```

## 15. When should you avoid parallel streams?

Avoid them for small workloads where splitting and coordination cost more than the work; blocking I/O; operations with shared mutable state or side effects; ordered workloads where preserving encounter order is expensive; or when the common ForkJoinPool is already busy or you need explicit control over the executor. Parallel streams work best for sufficiently large, CPU-bound, independent operations with associative reductions. Measure with representative workloads before choosing.

```java
// Safe reduction: independent mapping and associative sum
long total = values.parallelStream()
        .mapToLong(Integer::longValue)
        .sum();

// Avoid: shared mutable accumulator can race
// List<Integer> results = new ArrayList<>();
// values.parallelStream().forEach(results::add);
```
