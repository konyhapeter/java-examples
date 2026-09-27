package org.example.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {
//    https://www.baeldung.com/quartz
    @Bean
    public JobDetail quartzTaskJobDetail() {
        return JobBuilder.newJob(QuartzTasks.class)
                .withIdentity("quartzTask")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger quartzTaskTrigger(
            JobDetail quartzTaskJobDetail,
            @Value("${app.quartz.cron}") String cronExpression) {
        return TriggerBuilder.newTrigger()
                .forJob(quartzTaskJobDetail)
                .withIdentity("quartzTaskTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }
}
