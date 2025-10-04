package app.publishsubscribe;

import lombok.Getter;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class RedisSubscriber implements MessageListener {

    private CountDownLatch latch;

    @Getter
    private volatile String lastMessage;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        lastMessage = message.toString();
        this.latch.countDown();
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
