package com.pennant.backend.endofday.jobparameter;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

public class PFSJobParametersIncrementer implements JobParametersIncrementer {
	  
	public PFSJobParametersIncrementer() {
		
	}
	
	public JobParameters getNext(JobParameters parameters) {
		
		if (parameters==null || parameters.isEmpty()) {
			return new JobParametersBuilder().addLong("run.id", 1L).toJobParameters();
		}
		
		long id = parameters.getLong("run.id",1L) + 1;
		return new JobParametersBuilder().addLong("run.id", id).toJobParameters();
	}
}