package com.pennanttech.pff.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.cashback.CashBackDBDProcess;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CashBackDBDJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			getCashBackDBDProcess(context).autoCashBackProcess();
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

	}

	private CashBackDBDProcess getCashBackDBDProcess(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (CashBackDBDProcess) jobDataMap.get("cashBackDBDProcess");
	}
}
