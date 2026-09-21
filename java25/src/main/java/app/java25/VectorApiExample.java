package app.java25;

import jdk.incubator.vector.IntVector;

final class VectorApiExample {

    /**
     * Adds 25 to every lane of a preferred-width integer vector.
     */
    static void run() {
        var species = IntVector.SPECIES_PREFERRED;
        var values = new int[species.length()];
        var vector = IntVector.fromArray(species, values, 0).add(25);
        System.out.println("Vector API: " + vector.lane(0));
    }
}
