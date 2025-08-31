record Point(int x, int y) {
}

// Nested record patterns
record Rectangle(Point upperLeft, Point lowerRight) {
}

record Circle(Point center, int radius) {
}

static void printSum2(Object obj) {
    if (obj instanceof Point(int x, int y)) {
        System.out.println(x + y);
    }
}

static double calcArea(Object obj) {
    if (obj instanceof Rectangle(Point(int x1, int y1), Point(int x2, int y2))) {
        return (x2 - x1) * (y2 - y1);
    } else if (obj instanceof Circle(Point center, int radius)) {
        return Math.PI * radius * radius;
    }
    return 0.0;
}

static String getDayOfWeek(int day) {
    return switch (day) {
        case 1, 7 -> "Weekend";
        case 2, 3, 4, 5, 6 -> "Weekday";
        default -> throw new IllegalStateException("Invalid day: " + day);
    };
}


void main() {
    Point point = new Point(42, 42);
    Point secondPoint = new Point(10, 10);

    printSum2(point);

    System.out.println(calcArea(new Rectangle(point, secondPoint)));
    System.out.println(calcArea(new Circle(secondPoint, 10)));

    String json = """
            {
                "name": "John",
                "age": 30
            }
            """;

    System.out.println(json);

    RandomGenerator rng = RandomGenerator.of("L64X128MixRandom");
    System.out.println(rng.nextInt(100)); // random 0-99

    List<Integer> days = Arrays.asList(1, 2, 4, 6, 8);
    days.stream()
            .map(day -> {
                try {
                    return getDayOfWeek(day);
                } catch (Exception e) {
                    return "Invalid(" + day + ")";
                }
            })
            .forEach(System.out::println);

    //filter out with Optional
    days.stream()
            .map(day -> {
                try {
                    return Optional.of(getDayOfWeek(day));
                } catch (Exception e) {
                    return Optional.<String>empty();
                }
            })
            .flatMap(Optional::stream) // unwrap Optionals, skip empties
            .forEach(System.out::println);
}

