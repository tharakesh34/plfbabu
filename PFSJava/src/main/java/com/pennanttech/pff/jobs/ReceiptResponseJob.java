package com.pennanttech.pff.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.service.receipts.ReceiptResponseProcess;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ReceiptResponseJob extends AbstractJob {

	public ReceiptResponseJob() {
		super();
	}

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			ReceiptResponseProcess rrp = new ReceiptResponseProcess(getReceiptUploadHeaderService(context));
			rrp.processResponse();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private ReceiptUploadHeaderService getReceiptUploadHeaderService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (ReceiptUploadHeaderService) jobDataMap.get("receiptUploadHeaderService");
	}
}