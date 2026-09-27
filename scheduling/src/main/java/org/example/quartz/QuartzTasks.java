package org.example.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class QuartzTasks implements Job {
    private static final Logger log = LoggerFactory.getLogger(QuartzTasks.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Quartz job ran at {}", LocalTime.now());
    }
}
