package com.pennanttech.pff.jobs;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.service.receipts.ReceiptResponseProcess;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ReceiptResponseJob implements Job, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ReceiptResponseJob.class);

	public ReceiptResponseJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {

			if ("N".equals(SysParamUtil.getValueAsString("IS_RECEIPT_RESPONSE_JOB_REQ"))) {
				return;
			}

			ReceiptResponseProcess receiptResponseProcess = new ReceiptResponseProcess(
					(ReceiptUploadHeaderService) SpringBeanUtil.getBean("receiptUploadHeaderService"));
			receiptResponseProcess.processResponse();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}
}