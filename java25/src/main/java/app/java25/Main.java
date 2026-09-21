package app.java25;

public class Main {

    /**
     * Runs all Java 25 feature demonstrations in sequence.
     *
     * @param args command-line arguments, which are not used
     * @throws Exception if a demonstration encounters an error
     */
    public static void main(String[] args) throws Exception {
        FlexibleConstructor.run();
        ModuleImportExample.run();
        PrimitivePatterns.run();
        ScopedValuesExample.run();
        StableValuesExample.run();
        StructuredConcurrencyExample.run();
        KeyDerivationExample.run();
        VectorApiExample.run();
    }
}
