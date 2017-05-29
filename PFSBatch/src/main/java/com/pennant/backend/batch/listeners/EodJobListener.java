package com.pennant.backend.batch.listeners;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;


public class EodJobListener implements JobExecutionListener{
	
	
	public EodJobListener() {
		super();
	}
	
	@Override
	public void afterJob(JobExecution arg0) {
	}

	@Override
	public void beforeJob(JobExecution arg0) {
	}


}
