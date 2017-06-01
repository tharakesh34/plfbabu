package com.pennanttech.pff.bajaj.schedule.job;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.bajaj.services.PosidexResponseService;
import com.pennanttech.pff.core.Literal;

public class PosidexCustomerUpdateResponseJob implements Job, Serializable {
	private static final Logger				logger				= Logger.getLogger(PosidexCustomerUpdateResponseJob.class);

	private static final long				serialVersionUID	= 1L;

	private static PosidexResponseService	posidexResponseService;

	public PosidexCustomerUpdateResponseJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		posidexResponseService.execute(context);
		logger.debug(Literal.LEAVING);
	}

	public static void setPosidexResponseService(PosidexResponseService posidexResponseService) {
		PosidexCustomerUpdateResponseJob.posidexResponseService = posidexResponseService;
	}

}
