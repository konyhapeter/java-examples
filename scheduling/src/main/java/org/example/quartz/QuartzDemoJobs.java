package org.example.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
public class QuartzDemoJobs implements ApplicationRunner {
    private static final JobKey JOB_KEY = JobKey.jobKey("persistentDemo", "examples");

    private final Scheduler scheduler;
    private final boolean enabled;
    private final String cronExpression;

    public QuartzDemoJobs(
            Scheduler scheduler,
            @Value("${app.quartz.seed.enabled:true}") boolean enabled,
            @Value("${app.quartz.cron}") String cronExpression) {
        this.scheduler = scheduler;
        this.enabled = enabled;
        this.cronExpression = cronExpression;
    }

    @Override
    public void run(ApplicationArguments args) throws SchedulerException {
        if (!enabled || scheduler.checkExists(JOB_KEY)) {
            return;
        }

        JobDetail job = JobBuilder.newJob(QuartzTasks.class)
                .withIdentity(JOB_KEY)
                .withDescription("Persistent Quartz example with independent triggers")
                .usingJobData("taskName", "persistent-demo")
                .usingJobData("payload", "seeded at application startup")
                .requestRecovery(true)
                .storeDurably()
                .build();

        Trigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey("cron", "examples.persistentDemo"))
                .forJob(JOB_KEY)
                .withDescription("Cron trigger; missed executions are skipped")
                .startAt(new Date())
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
                        .withMisfireHandlingInstructionDoNothing())
                .build();

        Trigger intervalTrigger = TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey("interval", "examples.persistentDemo"))
                .forJob(JOB_KEY)
                .withDescription("Independent fixed-interval trigger")
                .startAt(new Date())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(30)
                        .repeatForever()
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();

        try {
            scheduler.scheduleJob(job, Set.of(cronTrigger, intervalTrigger), false);
        } catch (org.quartz.ObjectAlreadyExistsException concurrentStartup) {
            if (!scheduler.checkExists(JOB_KEY)) {
                throw concurrentStartup;
            }
        }
    }
}
