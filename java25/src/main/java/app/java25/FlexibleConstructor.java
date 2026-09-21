package app.java25;

final class FlexibleConstructor {

    /**
     * Demonstrates validation or normalization before assigning a record component.
     */
    static void run() {
        System.out.println("Flexible constructor body: " + new NormalizedText("  Java 25  ").value());
    }

    private record NormalizedText(String value) {

        /**
         * Trims the supplied text before the record state is initialized.
         *
         * @param value text to normalize
         */
        NormalizedText {
            value = value.trim();
        }
    }
}
