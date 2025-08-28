package org.example;

import org.awaitility.Durations;
import org.example.tasks.ScheduledTasks;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ScheduledTasksTest {

    @MockitoSpyBean
    ScheduledTasks tasks;

    @Test
    public void reportCurrentTime() {
        await().atMost(Durations.ONE_MINUTE).untilAsserted(() -> {
            verify(tasks, atLeast(5)).reportCurrentTime();
        });
    }
}
