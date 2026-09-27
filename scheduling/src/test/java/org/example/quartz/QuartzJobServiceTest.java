package org.example.quartz;

import org.example.quartz.QuartzJobRequest.MisfirePolicy;
import org.example.quartz.QuartzJobRequest.ScheduleType;
import org.example.quartz.QuartzJobRequest.TriggerRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuartzJobServiceTest {
    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private QuartzJobService jobService;

    @Test
    void createsRecoverableJobWithIndependentCronAndSimpleTriggers() throws Exception {
        when(scheduler.checkExists(any(TriggerKey.class))).thenReturn(false);
        when(scheduler.getTriggersOfJob(any())).thenReturn(List.of());

        QuartzJobRequest request = new QuartzJobRequest(
                "daily reconciliation",
                "reconcile",
                "tenant=demo",
                List.of(
                        new TriggerRequest("weekday", null, ScheduleType.CRON,
                                "0 0/5 9-17 ? * MON-FRI", "Europe/Budapest",
                                null, null, null, null, 5, MisfirePolicy.DO_NOTHING),
                        new TriggerRequest("retry", null, ScheduleType.SIMPLE,
                                null, null, 30_000L, -1,
                                null, null, 3, MisfirePolicy.NEXT_WITH_REMAINING_COUNT)));

        List<String> triggerKeys = jobService.upsertJob("billing", "reconcile", request);

        assertEquals(2, triggerKeys.size());
        ArgumentCaptor<JobDetail> jobCaptor = ArgumentCaptor.forClass(JobDetail.class);
        verify(scheduler).addJob(jobCaptor.capture(), org.mockito.ArgumentMatchers.eq(true),
                org.mockito.ArgumentMatchers.eq(true));
        assertTrue(jobCaptor.getValue().requestsRecovery());
        assertEquals("tenant=demo", jobCaptor.getValue().getJobDataMap().getString("payload"));

        ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);
        verify(scheduler, org.mockito.Mockito.times(2)).scheduleJob(triggerCaptor.capture());
        assertInstanceOf(CronTrigger.class, triggerCaptor.getAllValues().get(0));
        assertEquals(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING,
                triggerCaptor.getAllValues().get(0).getMisfireInstruction());
        assertInstanceOf(SimpleTrigger.class, triggerCaptor.getAllValues().get(1));
        assertEquals(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT,
                triggerCaptor.getAllValues().get(1).getMisfireInstruction());
    }

    @Test
    void rejectsCronMisfirePolicyBeforeChangingScheduler() throws Exception {
        QuartzJobRequest request = new QuartzJobRequest(
                null,
                "reconcile",
                null,
                List.of(new TriggerRequest("invalid", null, ScheduleType.CRON,
                        "0 0 * * * ?", null, null, null,
                        null, null, null, MisfirePolicy.FIRE_NOW)));

        assertThrows(IllegalArgumentException.class,
                () -> jobService.upsertJob("billing", "reconcile", request));
        verify(scheduler, never()).addJob(any(), anyBoolean(), anyBoolean());
    }

    @Test
    void keepsTriggerKeysDistinctWhenJobKeysContainSeparators() throws Exception {
        when(scheduler.checkExists(any(TriggerKey.class))).thenReturn(false);
        when(scheduler.getTriggersOfJob(any())).thenReturn(List.of());
        QuartzJobRequest request = new QuartzJobRequest(
                null,
                "task",
                null,
                List.of(new TriggerRequest("tick", null, ScheduleType.SIMPLE,
                        null, null, 1000L, -1, null, null,
                        null, MisfirePolicy.FIRE_NOW)));

        jobService.upsertJob("a.b", "c", request);
        jobService.upsertJob("a", "b.c", request);

        ArgumentCaptor<Trigger> triggers = ArgumentCaptor.forClass(Trigger.class);
        verify(scheduler, org.mockito.Mockito.times(2)).scheduleJob(triggers.capture());
        assertNotEquals(triggers.getAllValues().get(0).getKey(),
                triggers.getAllValues().get(1).getKey());
    }
}
