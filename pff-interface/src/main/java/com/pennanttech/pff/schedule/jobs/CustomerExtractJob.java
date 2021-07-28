package com.pennanttech.pff.schedule.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.process.ExtractCustomerData;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class CustomerExtractJob extends AbstractJob {
	private ExtractCustomerData extractCustomerData;

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			getExtractCustomerData(context).processDownloadCustomers();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private ExtractCustomerData getExtractCustomerData(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (ExtractCustomerData) jobDataMap.get("extractCustomerData");
	}

}
