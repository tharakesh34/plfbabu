package com.pennanttech.pff.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.external.gst.GSTInvoiceGeneratorService;

public class GSTInvoiceGeneratorJob implements org.quartz.Job {

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