package com.pennanttech.pff.external.disbursement;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DisbursementProcessJob extends AbstractJob {

	public DisbursementProcessJob() {
		super();
	}

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		getDisbursementRequestService(context).processRequests();

	}

	private DisbursementRequestService getDisbursementRequestService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (DisbursementRequestService) jobDataMap.get("disbursementRequestService");
	}

}
