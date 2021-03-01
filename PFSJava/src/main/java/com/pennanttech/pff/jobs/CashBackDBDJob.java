package com.pennanttech.pff.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.cashback.CashBackDBDProcess;

public class CashBackDBDJob implements Job {
	private static final Logger logger = LogManager.getLogger(CashBackDBDJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getKey().getName();
		logger.debug(String.format("JOB: %s", jobName));

		CashBackDBDProcess cashBackDBDProcess = null;

		try {
			cashBackDBDProcess = (CashBackDBDProcess) SpringBeanUtil.getBean("cashBackDBDProcess");
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		if (cashBackDBDProcess != null) {
			try {
				cashBackDBDProcess.autoCashBackProcess();
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
		}

	}
}
