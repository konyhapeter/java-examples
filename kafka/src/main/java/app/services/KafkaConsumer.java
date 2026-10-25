package app.services;

import app.wikimedia.WikimediaRecentChange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final ObjectMapper objectMapper;

    public KafkaConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topic}")
    public void consume(ConsumerRecord<String, String> record) {
        log.info("Received key={} value={} partition={} offset={}",
                record.key(), record.value(), record.partition(), record.offset());
    }

    @KafkaListener(topics = "${app.kafka.wikimedia-topic}")
    public void consumeWikimediaChange(ConsumerRecord<String, String> record) {
        try {
            WikimediaRecentChange change =
                    objectMapper.readValue(record.value(), WikimediaRecentChange.class);
            log.info("Processed Wikimedia change eventId={} wiki={} title={} type={} user={} "
                            + "partition={} offset={}",
                    change.eventId(), change.wiki(), change.title(), change.changeType(),
                    change.user(), record.partition(), record.offset());
        } catch (JsonProcessingException exception) {
            log.error("Invalid Wikimedia event JSON key={} partition={} offset={}",
                    record.key(), record.partition(), record.offset(), exception);
            throw new IllegalArgumentException("Invalid Wikimedia event JSON", exception);
        } catch (RuntimeException exception) {
            log.error("Failed to process Wikimedia event key={} partition={} offset={}",
                    record.key(), record.partition(), record.offset(), exception);
            throw exception;
        }
    }
}
