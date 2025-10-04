package app.publishsubscribe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisPubSubTest {

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private RedisSubscriber redisSubscriber;

    @Autowired
    private RedisMessageListenerContainer listenerContainer;

    private final ChannelTopic topic = new ChannelTopic("my-channel");

    @BeforeEach
    void setUp() {
        // make sure subscriber is registered
        listenerContainer.addMessageListener(redisSubscriber, topic);
    }

    @Test
    void testPublishSubscribe() throws InterruptedException {
        String message = "Hello Redis234!";
        CountDownLatch latch = new CountDownLatch(1);

        // wrap subscriber to count down when message is received
        RedisSubscriber testSubscriber = new RedisSubscriber();
        testSubscriber.setLatch(latch);
        listenerContainer.addMessageListener(testSubscriber, topic);

        // publish message
        redisPublisher.publish(topic.getTopic(), message);

        // wait up to 2 seconds for message
        boolean received = latch.await(2, TimeUnit.SECONDS);

        assertThat(received).isTrue();
        assertThat(testSubscriber.getLastMessage()).isEqualTo(message);
    }
}