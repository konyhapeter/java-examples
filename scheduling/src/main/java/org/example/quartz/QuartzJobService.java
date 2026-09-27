package org.example.quartz;

import org.example.quartz.QuartzJobRequest.MisfirePolicy;
import org.example.quartz.QuartzJobRequest.ScheduleType;
import org.example.quartz.QuartzJobRequest.TriggerRequest;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Date;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

@Service
public class QuartzJobService {
    private final Scheduler scheduler;

    public QuartzJobService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public List<String> upsertJob(String group, String name, QuartzJobRequest request)
            throws SchedulerException {
        JobKey jobKey = jobKey(group, name);
        validateRequest(request, group, name);

        JobDataMap jobData = new JobDataMap();
        jobData.put("taskName", request.taskName());
        jobData.put("payload", request.payload() == null ? "" : request.payload());

        JobDetail jobDetail = JobBuilder.newJob(QuartzTasks.class)
                .withIdentity(jobKey)
                .withDescription(request.description())
                .usingJobData(jobData)
                .requestRecovery(true)
                .storeDurably()
                .build();

        List<Trigger> desiredTriggers = request.triggers().stream()
                .map(trigger -> buildTrigger(jobKey, group, name, trigger))
                .toList();
        Set<TriggerKey> desiredKeys = new HashSet<>();
        for (Trigger trigger : desiredTriggers) {
            desiredKeys.add(trigger.getKey());
        }

        scheduler.addJob(jobDetail, true, true);

        for (Trigger trigger : desiredTriggers) {
            TriggerKey key = trigger.getKey();
            if (scheduler.checkExists(key)) {
                if (scheduler.rescheduleJob(key, trigger) == null) {
                    scheduleNewTrigger(trigger);
                }
            } else {
                scheduleNewTrigger(trigger);
            }
        }

        for (Trigger existing : scheduler.getTriggersOfJob(jobKey)) {
            if (!desiredKeys.contains(existing.getKey())) {
                scheduler.unscheduleJob(existing.getKey());
            }
        }

        return desiredTriggers.stream().map(trigger -> trigger.getKey().toString()).toList();
    }

    public boolean deleteJob(String group, String name) throws SchedulerException {
        return scheduler.deleteJob(jobKey(group, name));
    }

    private void scheduleNewTrigger(Trigger trigger) throws SchedulerException {
        try {
            scheduler.scheduleJob(trigger);
        } catch (ObjectAlreadyExistsException concurrentUpdate) {
            throw new SchedulerException(
                    "Trigger was created concurrently: " + trigger.getKey(), concurrentUpdate);
        }
    }

    private Trigger buildTrigger(
            JobKey jobKey,
            String group,
            String jobName,
            TriggerRequest request) {
        TriggerKey triggerKey = TriggerKey.triggerKey(
                request.name(), triggerGroupFor(jobKey));
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobKey)
                .withDescription(request.description())
                .withPriority(request.priority() == null ? Trigger.DEFAULT_PRIORITY : request.priority());

        if (request.startAt() == null) {
            builder.startNow();
        } else {
            builder.startAt(Date.from(request.startAt()));
        }
        if (request.endAt() != null) {
            builder.endAt(Date.from(request.endAt()));
        }

        return switch (request.type()) {
            case CRON -> builder.withSchedule(buildCronSchedule(request)).build();
            case SIMPLE -> builder.withSchedule(buildSimpleSchedule(request)).build();
        };
    }

    private CronScheduleBuilder buildCronSchedule(TriggerRequest request) {
        CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule(request.cronExpression());
        if (request.timeZone() != null && !request.timeZone().isBlank()) {
            schedule = schedule.inTimeZone(TimeZone.getTimeZone(ZoneId.of(request.timeZone())));
        }
        return switch (request.misfirePolicy()) {
            case DO_NOTHING -> schedule.withMisfireHandlingInstructionDoNothing();
            case FIRE_ONCE_NOW -> schedule.withMisfireHandlingInstructionFireAndProceed();
            case IGNORE_MISFIRES -> schedule.withMisfireHandlingInstructionIgnoreMisfires();
            default -> throw new IllegalArgumentException(
                    "Misfire policy " + request.misfirePolicy() + " is not valid for CRON triggers");
        };
    }

    private SimpleScheduleBuilder buildSimpleSchedule(TriggerRequest request) {
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMilliseconds(request.intervalMilliseconds())
                .withRepeatCount(request.repeatCount());
        return switch (request.misfirePolicy()) {
            case FIRE_NOW -> schedule.withMisfireHandlingInstructionFireNow();
            case NEXT_WITH_REMAINING_COUNT -> schedule.withMisfireHandlingInstructionNextWithRemainingCount();
            case NOW_WITH_REMAINING_COUNT -> schedule.withMisfireHandlingInstructionNowWithRemainingCount();
            case NEXT_WITH_EXISTING_COUNT -> schedule.withMisfireHandlingInstructionNextWithExistingCount();
            case NOW_WITH_EXISTING_COUNT -> schedule.withMisfireHandlingInstructionNowWithExistingCount();
            case IGNORE_MISFIRES -> schedule.withMisfireHandlingInstructionIgnoreMisfires();
            default -> throw new IllegalArgumentException(
                    "Misfire policy " + request.misfirePolicy() + " is not valid for SIMPLE triggers");
        };
    }

    private void validateRequest(QuartzJobRequest request, String group, String name) {
        if (request == null) {
            throw new IllegalArgumentException("A job request is required");
        }
        requireName(group, "Job group");
        requireName(name, "Job name");
        if (group.length() > 200 || name.length() > 200) {
            throw new IllegalArgumentException("Job group and name must be at most 200 characters");
        }
        requireName(request.taskName(), "Task name");
        if (request.taskName().length() > 200) {
            throw new IllegalArgumentException("Task name must be at most 200 characters");
        }
        if (request.description() != null && request.description().length() > 250) {
            throw new IllegalArgumentException("Job description must be at most 250 characters");
        }
        if (request.triggers() == null || request.triggers().isEmpty()) {
            throw new IllegalArgumentException("At least one trigger is required");
        }

        Set<String> triggerNames = new HashSet<>();
        for (TriggerRequest trigger : request.triggers()) {
            validateTrigger(trigger, triggerNames);
        }
    }

    private void validateTrigger(TriggerRequest trigger, Set<String> triggerNames) {
        if (trigger == null) {
            throw new IllegalArgumentException("Trigger definitions cannot be null");
        }
        requireName(trigger.name(), "Trigger name");
        if (trigger.name().length() > 200 || !triggerNames.add(trigger.name())) {
            throw new IllegalArgumentException("Trigger names must be unique and at most 200 characters");
        }
        if (trigger.description() != null && trigger.description().length() > 250) {
            throw new IllegalArgumentException("Trigger description must be at most 250 characters");
        }
        if (trigger.type() == null || trigger.misfirePolicy() == null) {
            throw new IllegalArgumentException("Trigger type and misfire policy are required");
        }
        if (trigger.startAt() != null && trigger.endAt() != null
                && !trigger.endAt().isAfter(trigger.startAt())) {
            throw new IllegalArgumentException("Trigger endAt must be after startAt");
        }

        if (trigger.type() == ScheduleType.CRON) {
            if (trigger.cronExpression() == null
                    || trigger.cronExpression().length() > 120
                    || !CronExpression.isValidExpression(trigger.cronExpression())) {
                throw new IllegalArgumentException(
                        "A valid cronExpression of at most 120 characters is required for CRON triggers");
            }
            if (trigger.intervalMilliseconds() != null || trigger.repeatCount() != null) {
                throw new IllegalArgumentException(
                        "CRON triggers cannot specify intervalMilliseconds or repeatCount");
            }
            requirePolicy(trigger.misfirePolicy(),
                    MisfirePolicy.DO_NOTHING, MisfirePolicy.FIRE_ONCE_NOW, MisfirePolicy.IGNORE_MISFIRES);
            if (trigger.timeZone() != null && !trigger.timeZone().isBlank()) {
                if (trigger.timeZone().length() > 80) {
                    throw new IllegalArgumentException("timeZone must be at most 80 characters");
                }
                try {
                    ZoneId.of(trigger.timeZone());
                } catch (DateTimeException invalidZone) {
                    throw new IllegalArgumentException("Invalid timeZone: " + trigger.timeZone(), invalidZone);
                }
            }
        } else {
            if (trigger.intervalMilliseconds() == null || trigger.intervalMilliseconds() <= 0
                    || trigger.repeatCount() == null || trigger.repeatCount() < SimpleTrigger.REPEAT_INDEFINITELY) {
                throw new IllegalArgumentException(
                        "SIMPLE triggers require a positive intervalMilliseconds and repeatCount >= -1");
            }
            if (trigger.cronExpression() != null || trigger.timeZone() != null) {
                throw new IllegalArgumentException("SIMPLE triggers cannot specify cronExpression or timeZone");
            }
            requirePolicy(trigger.misfirePolicy(),
                    MisfirePolicy.FIRE_NOW,
                    MisfirePolicy.NEXT_WITH_REMAINING_COUNT,
                    MisfirePolicy.NOW_WITH_REMAINING_COUNT,
                    MisfirePolicy.NEXT_WITH_EXISTING_COUNT,
                    MisfirePolicy.NOW_WITH_EXISTING_COUNT,
                    MisfirePolicy.IGNORE_MISFIRES);
        }
    }

    private void requirePolicy(MisfirePolicy actual, MisfirePolicy... allowed) {
        for (MisfirePolicy candidate : allowed) {
            if (actual == candidate) {
                return;
            }
        }
        throw new IllegalArgumentException(
                "Misfire policy " + actual + " is not valid for this trigger type");
    }

    private void requireName(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    private JobKey jobKey(String group, String name) {
        requireName(group, "Job group");
        requireName(name, "Job name");
        if (group.length() > 200 || name.length() > 200) {
            throw new IllegalArgumentException("Job group and name must be at most 200 characters");
        }
        return JobKey.jobKey(name, group);
    }

    private String triggerGroupFor(JobKey jobKey) {
        byte[] identity = (jobKey.getGroup() + "\0" + jobKey.getName())
                .getBytes(StandardCharsets.UTF_8);
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(identity);
            return "runtime-" + HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
