package app.java25;

import java.lang.ScopedValue;

final class ScopedValuesExample {

    private static final ScopedValue<String> USER = ScopedValue.newInstance();

    /**
     * Binds a user name for the duration of a scoped computation.
     */
    static void run() {
        var message = ScopedValue.where(USER, "alice").call(ScopedValuesExample::currentUserMessage);
        System.out.println("Scoped value: " + message);
    }

    /**
     * Creates a message using the user available in the current scope.
     *
     * @return a message containing the scoped user name
     */
    private static String currentUserMessage() {
        return "request handled for " + USER.get();
    }
}
