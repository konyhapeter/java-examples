package app;

import app.config.RedisConfig;
import app.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTests {

    @Autowired
    @Qualifier(RedisConfig.STRING_REDIS_TEMPLATE_NAME)
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    @Qualifier(RedisConfig.OBJECT_REDIS_TEMPLATE_NAME)
    private RedisTemplate<String, Object> objectRedisTemplate;

    @Test
    void testRedis() {
        redisTemplate.opsForValue().set("email", "something@gmail.com");

        Object email = redisTemplate.opsForValue().get("email");
        Object salary = redisTemplate.opsForValue().get("salary");

        //objectRedisTemplate.opsForValue().setIfAbsent("user", new User("apacuka", "fundaluka"));
        User user = (User) objectRedisTemplate.opsForValue().get("user");
        user.toString();
    }
}
