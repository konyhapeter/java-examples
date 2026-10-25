package app.services;

import app.wikimedia.WikimediaRecentChange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final String wikimediaTopic;
    private final ObjectMapper objectMapper;

    public KafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.topic}") String topic,
            @Value("${app.kafka.wikimedia-topic}") String wikimediaTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
        this.wikimediaTopic = wikimediaTopic;
    }

    public CompletableFuture<SendResult<String, String>> sendMessage(String key, String message) {
        return kafkaTemplate.send(topic, key, message);
    }

    public CompletableFuture<SendResult<String, String>> publishWikimediaChange(
            WikimediaRecentChange change) {
        try {
            String json = objectMapper.writeValueAsString(change);
            return kafkaTemplate.send(wikimediaTopic, change.wiki() + ":" + change.title(), json);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Could not serialize Wikimedia change", exception);
        }
    }
}
