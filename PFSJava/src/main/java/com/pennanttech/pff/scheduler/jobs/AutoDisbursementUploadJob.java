package com.pennanttech.pff.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pff.external.DisbursementResponse;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoDisbursementUploadJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		getDisbursementReponseProcess(context).processAutoResponseFiles();

	}

	private DisbursementResponse getDisbursementReponseProcess(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (DisbursementResponse) jobDataMap.get("disbursementResponse");
	}

}
