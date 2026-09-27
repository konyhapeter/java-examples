package org.example.quartz;

import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {
    @Bean
    public SchedulerFactoryBeanCustomizer quartzSchedulerListenerCustomizer() {
        return factory -> factory.setSchedulerListeners(new QuartzSchedulerListener());
    }
}
