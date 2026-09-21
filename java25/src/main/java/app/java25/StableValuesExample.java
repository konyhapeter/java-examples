package app.java25;

import java.lang.StableValue;

final class StableValuesExample {

    private static final StableValue<String> CONFIGURATION = StableValue.of();

    /**
     * Initializes the stable configuration value on first access and prints it.
     */
    static void run() {
        var configuration = CONFIGURATION.orElseSet(() -> "loaded once");
        System.out.println("Stable value: " + configuration);
    }
}
