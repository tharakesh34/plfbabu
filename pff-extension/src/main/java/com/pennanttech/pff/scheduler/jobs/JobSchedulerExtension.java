package com.pennanttech.pff.scheduler.jobs;

import java.util.List;

import com.pennanttech.pennapps.core.job.scheduler.JobData;

public interface JobSchedulerExtension {
	List<JobData> loadJobs();
}
