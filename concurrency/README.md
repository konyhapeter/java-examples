# Virtual threads vs. WebFlux

This module runs two HTTP servers with the same `GET /io?delayMs=150` contract:

- **Virtual threads:** Spring MVC/Tomcat with `spring.threads.virtual.enabled=true`.
  The synchronous handler waits with `Thread.sleep`; Tomcat serves the request
  on a virtual thread.
- **WebFlux:** the reactive handler returns a `Mono` that completes after
  `Mono.delay`, without blocking its event-loop thread while the simulated I/O
  is pending.

This repository inherits both Spring MVC and WebFlux dependencies from its
parent POM. Spring Boot therefore currently hosts the reactive server on
Tomcat; the comparison is between MVC with virtual-thread request handling and
the WebFlux programming model, not between Tomcat and Reactor Netty.

The response identifies the implementation and whether the response-producing
thread is virtual. The delay is a controlled stand-in for waiting on a remote
service; it is not real network I/O and does not model connection pools,
downstream contention, payload processing, or backpressure.

## Run both endpoints

From the repository root:

```shell
mvn -pl concurrency package
java -jar concurrency/target/concurrency-1.0-SNAPSHOT.jar
```

The virtual-thread endpoint listens on port 8081 and WebFlux listens on 8082.
Try them with:

```shell
curl "http://localhost:8081/io?delayMs=150"
curl "http://localhost:8082/io?delayMs=150"
```

Ports can be changed with `-Dcomparison.virtual-threads.port=...` and
`-Dcomparison.webflux.port=...`. The allowed delay is 0 through 1000 ms.

## Integration load comparison

Run:

```shell
mvn -pl concurrency test
```

The integration test starts both servers, checks response equivalence and the
virtual-thread behavior, then issues concurrent HTTP requests to each server.
It prints throughput and p50/p95/p99 request latency for three rounds,
alternating which implementation runs first to reduce simple order bias. It
warms both servers first and verifies every request succeeds.

This is an executable comparison, not a statistically rigorous benchmark.
Assertions check correctness, not that one approach beats the other: absolute
timings depend on CPU load, JVM warmup, OS scheduling, and the HTTP client.
For more reliable measurements, use an otherwise idle machine, keep the JDK
and machine fixed, increase the request count, repeat runs, and compare
distributions rather than one result. Do not treat the synthetic wait as a
substitute for testing your actual downstream service and connection limits.

## Inspect with JFR

Build and launch with a recording:

```shell
mvn -pl concurrency package
java -XX:StartFlightRecording=filename=concurrency.jfr,settings=profile,dumponexit=true -jar concurrency/target/concurrency-1.0-SNAPSHOT.jar
```

Warm the endpoints, then send load to one port at a time so each interval is
easy to identify in the recording. For example, use a load generator against
port 8081, pause, then run the same load against port 8082. Stop the JVM to
write the recording. Inspect `concurrency.jfr` with Java Mission Control, or
start with:

```shell
jfr summary concurrency.jfr
```

Look at thread/virtual-thread activity, execution samples, allocation, GC
pauses, and socket events. A short synthetic wait may not produce meaningful
CPU samples; increase the load duration or profile a representative downstream
call before drawing conclusions.
