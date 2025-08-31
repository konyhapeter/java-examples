sealed interface Shape permits Circle, Rectangle, Square {
    double area();
}

record Circle(double radius) implements Shape {
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

record Rectangle(double length, double width) implements Shape {
    @Override
    public double area() {
        return length * width;
    }
}

record Square(double side) implements Shape {
    @Override
    public double area() {
        return side * side;
    }
}

//sealed interface does not allow it
//record Triangle(double base, double height) implements Shape {
//    @Override
//    public double area() {
//        return 0.5 * base * height;
//    }
//}

static double calcArea(Shape shape) {
    return switch (shape) {
        case Circle c -> c.area();
        case Rectangle r -> r.area();
        case Square s -> s.area();
    };
}
// As of Java 21
static String formatterPatternSwitch(Object obj) {
    return switch (obj) {
        case Integer i -> String.format("int %d", i);
        case Long l    -> String.format("long %d", l);
        case Double d  -> String.format("double %f", d);
        case String s  -> String.format("String %s", s);
        default        -> obj.toString();
    };
}

// case dominance - does not compile
static String checkShapeType(Object object) {
    return switch (object) {
        // case Shape s -> s.getClass().getSimpleName();
        case Circle c -> "Circle";
        case Rectangle r -> "Rectangle";
        case Square s -> "Square";
        case Shape s -> s.getClass().getSimpleName();
        default -> "Unknown";
    };
}
void main() {
}

