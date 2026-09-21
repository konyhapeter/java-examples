package app.java25;

final class PrimitivePatterns {

    /**
     * Demonstrates matching primitive values directly in a type pattern switch.
     */
    static void run() {
        System.out.println("Primitive pattern: " + describe(42));
        System.out.println("Primitive pattern: " + describe(42L));
        System.out.println("Primitive pattern: " + describe(3.14));
    }

    /**
     * Converts a value to a description based on its runtime type.
     *
     * @param value value to describe
     * @return a type and value description
     */
    private static String describe(Object value) {
        return switch (value) {
            case int number -> "int " + number;
            case long number -> "long " + number;
            case double number -> "double " + number;
            case String text -> "String " + text;
            case null -> "null";
            default -> "other";
        };
    }
}
