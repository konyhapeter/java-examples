package app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaTopicConfiguration {

    private static final Logger log = LoggerFactory.getLogger(KafkaTopicConfiguration.class);

    @Bean
    NewTopic ordersTopic(@Value("${app.kafka.topic}") String topic) {
        return topic(topic);
    }

    @Bean
    NewTopic wikimediaTopic(@Value("${app.kafka.wikimedia-topic}") String topic) {
        return topic(topic);
    }

    @Bean
    NewTopic deadLetterTopic(@Value("${app.kafka.dead-letter-topic}") String topic) {
        return topic(topic);
    }

    @Bean
    CommonErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.dead-letter-topic}") String deadLetterTopic,
            @Value("${app.kafka.retry-interval-ms}") long retryIntervalMs,
            @Value("${app.kafka.retry-count}") long retryCount) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> {
                    log.error("Sending failed record from {} partition {} offset {} to {}",
                            record.topic(), record.partition(), record.offset(), deadLetterTopic,
                            exception);
                    return new TopicPartition(deadLetterTopic, record.partition());
                });
        return new DefaultErrorHandler(recoverer, new FixedBackOff(retryIntervalMs, retryCount));
    }

    private NewTopic topic(String topic) {
        return TopicBuilder.name(topic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
