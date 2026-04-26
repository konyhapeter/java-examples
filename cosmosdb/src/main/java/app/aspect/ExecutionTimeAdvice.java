package app.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Aspect
@Component
@Slf4j
//@ConditionalOnExpression("${aspect.enabled:true}")
public class ExecutionTimeAdvice {

    @Around("@annotation(app.aspect.TrackExecutionTime) || @within(app.aspect.TrackExecutionTime)")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        Instant start = Instant.now();
        Object object = point.proceed();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        log.info("Class Name: {}. Method Name: {}. Time taken for Execution is : {}millis", point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), duration.toMillis());
        return object;
    }
}
