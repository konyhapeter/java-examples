package org.example.quartz;

import java.time.Instant;
import java.util.List;

public record QuartzJobRequest(
        String description,
        String taskName,
        String payload,
        List<TriggerRequest> triggers) {

    public enum ScheduleType {
        CRON,
        SIMPLE
    }

    public enum MisfirePolicy {
        DO_NOTHING,
        FIRE_ONCE_NOW,
        FIRE_NOW,
        NEXT_WITH_REMAINING_COUNT,
        NOW_WITH_REMAINING_COUNT,
        NEXT_WITH_EXISTING_COUNT,
        NOW_WITH_EXISTING_COUNT,
        IGNORE_MISFIRES
    }

    public record TriggerRequest(
            String name,
            String description,
            ScheduleType type,
            String cronExpression,
            String timeZone,
            Long intervalMilliseconds,
            Integer repeatCount,
            Instant startAt,
            Instant endAt,
            Integer priority,
            MisfirePolicy misfirePolicy) {
    }
}
