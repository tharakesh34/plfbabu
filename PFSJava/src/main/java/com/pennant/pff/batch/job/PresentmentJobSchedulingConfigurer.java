package com.pennant.pff.batch.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
@ComponentScan("com.pennant.pff.presentment")
public class PresentmentJobSchedulingConfigurer implements SchedulingConfigurer {

	@Bean
	public ThreadPoolTaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(1);
		scheduler.setThreadGroupName("PRMNT_RESPONSE_JOB_SCHEDULERS");
		scheduler.setThreadNamePrefix("PRMNT_RESPONSE_SCHEDULERS");
		// scheduler.setAwaitTerminationSeconds();
		return scheduler;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		TaskScheduler scheduler = this.taskScheduler();
		taskRegistrar.setTaskScheduler(scheduler);
	}

}
