# Event-driven Kafka examples

This module demonstrates asynchronous communication using Spring Boot, Kafka,
and JSON messages. It contains a keyed order-message example and a Wikimedia
recent-changes pipeline.

## Architecture

The Wikimedia example follows an event-driven flow:

1. `WikimediaEventStream` subscribes to Wikimedia's public RecentChange
   EventStreams endpoint using Server-Sent Events (SSE).
2. Incoming events are mapped to the module's `WikimediaRecentChange` JSON
   event and published asynchronously to `wiki.recent-changes`.
3. The Kafka key is `wiki:title`, keeping changes for the same page in the same
   partition and preserving their partition order.
4. A consumer group processes the JSON event independently from the source.
   Processing failures are retried and then sent to
   `wiki.recent-changes.dlt`.

The consumer logs each event with its Kafka partition and offset. Kafka
delivery is at least once, so downstream side effects should be idempotent;
`eventId` can be used as a deduplication key. This sample does not claim
exactly-once processing.

## Configuration

`src/main/resources/application.yml` configures Kafka's bootstrap server,
consumer group, string key/value serializers, topic names, and listener
acknowledgement mode, and retry interval/count. The dead-letter policy is wired
in `KafkaTopicConfiguration`. Override the broker using
`KAFKA_BOOTSTRAP_SERVERS`. The Wikimedia connector is disabled by default so
the application starts without an external HTTP stream. Enable it with
`WIKIMEDIA_ENABLED=true`; optionally override the SSE URL with
`WIKIMEDIA_EVENTSTREAM_URL`.

## Run locally

Docker is required to run the local broker and integration tests. Start Kafka:

```shell
docker compose up -d kafka
```

From the repository root, run the application:

```shell
mvn -pl kafka spring-boot:run
```

Publish a keyed order message:

```shell
curl -X POST "http://localhost:8080/kafka/publish?key=order-42&message=created"
```

Publish a JSON Wikimedia event without enabling the live SSE source:

```shell
curl -X POST "http://localhost:8080/kafka/wikimedia" `
  -H "Content-Type: application/json" `
  -d '{"eventId":"event-123","wiki":"enwiki","title":"Spring_Framework","changeType":"edit","user":"ExampleEditor","occurredAt":"2026-10-25T18:00:00Z","pageUrl":"https://en.wikipedia.org/wiki/Spring_Framework","revisionId":987654}'
```

Enable the live public stream when starting the application:

```powershell
$env:WIKIMEDIA_ENABLED="true"
mvn -pl kafka spring-boot:run
```

After stopping the application, disable it with
`Remove-Item Env:WIKIMEDIA_ENABLED`.

## Failure handling

Malformed Wikimedia JSON and processing exceptions are logged with the Kafka
key, partition, and offset. The listener retries twice with a one-second delay;
after that, the original record is published to `wiki.recent-changes.dlt` for
inspection or later replay. The live source reconnects after stream failures
with exponential backoff. Records accepted by Kafka are acknowledged
asynchronously, and the HTTP endpoint response includes topic, partition,
offset, and key.

## Integration tests

The Testcontainers test starts an isolated Kafka broker, exercises both the
string order message and JSON Wikimedia event, and verifies consumer delivery:

```shell
mvn -pl kafka test
```

The test requires a working Docker daemon and does not depend on a separately
running local Kafka instance.
