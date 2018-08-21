package com.pennanttech.pff.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.external.gst.GSTInvoiceGeneratorService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class GSTInvoiceGeneratorJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		GSTInvoiceGeneratorService invoiceGenerator = null;
		
		try {
			invoiceGenerator = (GSTInvoiceGeneratorService) SpringBeanUtil.getBean("gstInvoiceGenerator");
		} catch (Exception e) {
			//do nothing
		}
		
		if (invoiceGenerator == null) {
			invoiceGenerator = (GSTInvoiceGeneratorService) SpringBeanUtil.getBean("defaultGstInvoiceGenerator");
		}
		
		if (invoiceGenerator != null) {
			invoiceGenerator.generateInvoice();
		}
	}
}