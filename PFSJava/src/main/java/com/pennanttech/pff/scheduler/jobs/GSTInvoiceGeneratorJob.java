package com.pennanttech.pff.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.job.JobExecution;
import com.pennanttech.pennapps.core.job.JobStatusMap.MapConstants;
import com.pennanttech.pff.external.gst.GSTInvoiceGeneratorService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class GSTInvoiceGeneratorJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		JobExecution jobExecution = getGSTInvoiceGeneratorService(context).generateInvoice();

		if (jobExecution != null) {
			context.put(MapConstants.JOB_EXECUTION, jobExecution);
		}
	}

	private GSTInvoiceGeneratorService getGSTInvoiceGeneratorService(JobExecutionContext context) {
		GSTInvoiceGeneratorService invoiceGenerator = null;

		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		invoiceGenerator = (GSTInvoiceGeneratorService) jobDataMap.get("gstInvoiceGenerator");

		if (invoiceGenerator == null) {
			invoiceGenerator = (GSTInvoiceGeneratorService) jobDataMap.get("defaultGstInvoiceGenerator");
		}

		return invoiceGenerator;
	}
}