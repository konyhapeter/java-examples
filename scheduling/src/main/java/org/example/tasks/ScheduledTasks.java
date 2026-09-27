package org.example.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

//    @Scheduled(cron = "${app.cron}")
    public void reportCurrentTime() {
        log.info("The time is now {}", LocalTime.now());
    }

    @Scheduled(fixedRate = 1)
    public void reportAtFixedRate() throws InterruptedException {
        log.info("Spring Scheduler fixed-rate task ran at {}", LocalTime.now());
        Thread.sleep(1000);
    }

//    @Schedules({
//            @Scheduled(fixedDelay = 1000, scheduler = "customTaskScheduler"),
//            @Scheduled(cron = "0 * * * * MON-FRI")
//    })
    public void reportAfterFixedDelay() throws InterruptedException {
        log.info("Spring Scheduler fixed-delay task ran at {}", LocalTime.now());
        Thread.sleep(100);
    }
}
