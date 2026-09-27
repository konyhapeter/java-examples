package org.example.quartz;

import org.quartz.SchedulerException;
import org.quartz.listeners.SchedulerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzSchedulerListener extends SchedulerListenerSupport {
    private static final Logger log = LoggerFactory.getLogger(QuartzSchedulerListener.class);

    @Override
    public void schedulerStarted() {
        log.info("Quartz scheduler started");
    }

    @Override
    public void schedulerShutdown() {
        log.info("Quartz scheduler shut down");
    }

    @Override
    public void schedulerError(String message, SchedulerException cause) {
        log.error("Quartz scheduler error: {}", message, cause);
    }
}
