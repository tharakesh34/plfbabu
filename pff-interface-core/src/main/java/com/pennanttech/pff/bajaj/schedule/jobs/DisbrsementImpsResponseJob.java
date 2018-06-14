package com.pennanttech.pff.bajaj.schedule.jobs;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.bajaj.services.DisbursementIMPSResponseService;
import com.pennanttech.pennapps.core.resource.Literal;

public class DisbrsementImpsResponseJob implements Job, Serializable {
	private static final Logger logger = Logger.getLogger(DisbrsementImpsResponseJob.class);

	private static final long serialVersionUID = 1L;

	private static DisbursementIMPSResponseService disbursementIMPSResponseService;

	public DisbrsementImpsResponseJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		disbursementIMPSResponseService.execute(context);
		logger.debug(Literal.LEAVING);
	}

	public static void setDisbursementIMPSResponseService(DisbursementIMPSResponseService disbursementIMPSResponseService) {
		DisbrsementImpsResponseJob.disbursementIMPSResponseService = disbursementIMPSResponseService;
	}

}
