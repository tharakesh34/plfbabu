package com.pennanttech.pff.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pff.external.gst.GSTInvoiceGeneratorService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class GSTInvoiceGeneratorJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {

		getGSTInvoiceGeneratorService(context).generateInvoice();
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