package com.pennanttech.pff.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.backend.service.finance.NonLanReceiptService;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MobAgencyReciptLimitUpdateJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			getNonLanReceiptService(context).processCollectionAPILog();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private NonLanReceiptService getNonLanReceiptService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (NonLanReceiptService) jobDataMap.get("nonLanReceiptService");

	}

}
