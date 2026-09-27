package org.example.quartz;

import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/quartz/jobs")
public class QuartzJobController {
    private final QuartzJobService jobService;

    public QuartzJobController(QuartzJobService jobService) {
        this.jobService = jobService;
    }

    @PutMapping("/{group}/{name}")
    public JobScheduleResponse upsertJob(
            @PathVariable String group,
            @PathVariable String name,
            @RequestBody QuartzJobRequest request) throws SchedulerException {
        List<String> triggers = jobService.upsertJob(group, name, request);
        return new JobScheduleResponse(name, group, triggers);
    }

    @DeleteMapping("/{group}/{name}")
    public void deleteJob(@PathVariable String group, @PathVariable String name)
            throws SchedulerException {
        if (!jobService.deleteJob(group, name)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quartz job was not found");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> invalidRequest(IllegalArgumentException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.badRequest().body(problem);
    }

    public record JobScheduleResponse(String name, String group, List<String> triggers) {
    }
}
