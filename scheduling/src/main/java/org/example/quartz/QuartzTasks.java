package org.example.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.DisallowConcurrentExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class QuartzTasks implements Job {
    private static final Logger log = LoggerFactory.getLogger(QuartzTasks.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String taskName = context.getMergedJobDataMap().getString("taskName");
        String payload = context.getMergedJobDataMap().getString("payload");
        log.info("Quartz task '{}' ran at {}; recovering={}, payload={}",
                taskName,
                context.getFireTime(),
                context.isRecovering(),
                payload);
    }
}
