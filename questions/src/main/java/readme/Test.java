void main() {
    System.out.println("Hello, World!");

    record UserId(String value) {
    } // generated equals() and hashCode() use value

    record Team(String name, List<String> members) {
    }
    //    A record is only shallowly immutable: referenced objects may still be mutable.
    List<String> list = new ArrayList<>(List.of("Alice"));
    Team team = new Team("Core", list);

    // Cannot reassign team.members(), because it is final:
    // team.members = new ArrayList<>(); // Compile error

    // Can still mutate the underlying list!
    list.add("Bob");
    team.members().add("Charlie");

    System.out.println(team.members()); // [Alice, Bob, Charlie]

    Set<UserId> ids = new HashSet<>();
    ids.add(new UserId("u-1"));
    System.out.println(ids.contains(new UserId("u-1"))); // true

    Map<String, Integer> map = new HashMap<>();
    map.put("Aa", 1);
    map.put("BB", 2); // "Aa" and "BB" have the same String hash code, but are not equal
    System.out.println(map.size()); // 2

    System.out.println(area(new Circle(2))); // 12.566370614359172
    System.out.println(area(new Rectangle(3, 4)));

    System.out.println(describe(new Success("It worked!")));
    System.out.println(describe(new Failure("Something went wrong.")));

    List<String> myList = Stream.of("a", "b")
            .map(String::toUpperCase)
            .collect(Collectors.toList());

    System.out.println(myList); // [A, B]

    List<List<String>> groups = List.of(List.of("a", "b"), List.of("c"));
    List<String> all = groups.stream()
            .flatMap(List::stream)
            .toList(); // [a, b, c]
    System.out.println(all);


    //java 8
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

sealed interface Shape permits Circle, Rectangle {
}

record Circle(double radius) implements Shape {
}

record Rectangle(double width, double height) implements Shape {
}

static double area(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
    };
}

sealed interface Result permits Success, Failure { }
record Success(String value) implements Result { }
record Failure(String reason) implements Result { }

static String describe(Result result) {
    return switch (result) {
        case Success s -> s.value();
        case Failure f -> "Error: " + f.reason();
    };
}

class Animal {
}

class Dog extends Animal {
}

class Kennel {
    Animal adopt() {
        return new Animal();
    }
}

class DogKennel extends Kennel {
    // Covariant return type: Dog is a subclass of Animal
    @Override
    Dog adopt() {
        return new Dog();
    }
}
//java 8
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
