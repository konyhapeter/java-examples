package app.controller;

import app.services.KafkaProducer;
import app.wikimedia.WikimediaRecentChange;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducer producer;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/publish")
    public CompletableFuture<MessageAcknowledgement> sendMessage(
            @RequestParam String key,
            @RequestParam String message) {
        return producer.sendMessage(key, message)
                .thenApply(result -> acknowledgement(result, key));
    }

    @PostMapping("/wikimedia")
    public CompletableFuture<MessageAcknowledgement> publishWikimediaChange(
            @RequestBody WikimediaRecentChange change) {
        String key = change.wiki() + ":" + change.title();
        return producer.publishWikimediaChange(change)
                .thenApply(result -> acknowledgement(result, key));
    }

    private MessageAcknowledgement acknowledgement(
            SendResult<String, String> result, String key) {
        return new MessageAcknowledgement(
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset(),
                key);
    }

    public record MessageAcknowledgement(String topic, int partition, long offset, String key) {
    }
}
