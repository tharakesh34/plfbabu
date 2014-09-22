package com.pennant.backend.service.dashboard;

import org.quartz.Job;

public interface JobShedule  extends Job{

	  abstract void execute(org.quartz.JobExecutionContext arg0) throws org.quartz.JobExecutionException;
}
