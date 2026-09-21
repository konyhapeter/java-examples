# Java 25 examples

This module contains small, focused examples of the features delivered in
Java 25. Preview features are compiled with `--enable-preview`.

| Example | Java 25 feature |
| --- | --- |
| `CompactSourceFile` | Compact source files and instance `main` methods (JEP 512) |
| `FlexibleConstructor` | Flexible constructor bodies (JEP 513) |
| `ModuleImportExample` | Module import declarations (JEP 511, preview) |
| `PrimitivePatterns` | Primitive types in patterns (JEP 507, preview) |
| `ScopedValuesExample` | Scoped values (JEP 506) |
| `StableValuesExample` | Stable values (JEP 502, preview) |
| `StructuredConcurrencyExample` | Structured concurrency (JEP 505, preview) |
| `KeyDerivationExample` | Key Derivation Function API (JEP 510) |
| `VectorApiExample` | Vector API, tenth incubation (JEP 508) |

JEP 509 adds Linux CPU-time profiling to JFR. Enable it when starting an
application with `-XX:StartFlightRecording=jdk.CPUTimeSample#enabled=true`.

Run the examples with:

```text
mvn -pl java25 compile
java --enable-preview --add-modules jdk.incubator.vector -cp java25/target/classes app.java25.Main
```
