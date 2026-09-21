package app.java25;

import javax.crypto.KDF;
import javax.crypto.spec.HKDFParameterSpec;
import java.util.HexFormat;

final class KeyDerivationExample {

    /**
     * Derives a 16-byte key from input material using HKDF-SHA256.
     *
     * @throws Exception if the KDF algorithm or its parameters are unavailable
     */
    static void run() throws Exception {
        var kdf = KDF.getInstance("HKDF-SHA256");
        var parameters = HKDFParameterSpec.ofExtract()
                .addIKM("secret input".getBytes())
                .thenExpand("java25-example".getBytes(), 16);
        var derived = kdf.deriveData(parameters);
        System.out.println("Derived key: " + HexFormat.of().formatHex(derived));
    }
}
