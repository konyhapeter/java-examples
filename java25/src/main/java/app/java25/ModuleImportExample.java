package app.java25;

import module java.base;

final class ModuleImportExample {

    /**
     * Demonstrates importing all public types from a Java module.
     */
    static void run() {
        var values = List.of("Java", "25");
        System.out.println("Module import declaration: " + String.join(" ", values));
    }
}
