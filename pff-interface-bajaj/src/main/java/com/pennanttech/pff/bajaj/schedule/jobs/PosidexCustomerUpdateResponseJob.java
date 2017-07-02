package com.pennanttech.pff.bajaj.schedule.jobs;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.bajaj.services.PosidexResponseServiceImpl;
import com.pennanttech.pff.core.Literal;

public class PosidexCustomerUpdateResponseJob implements Job, Serializable {
	private static final Logger				logger				= Logger.getLogger(PosidexCustomerUpdateResponseJob.class);

	private static final long				serialVersionUID	= 1L;

	private static PosidexResponseServiceImpl	posidexResponseService;

	public PosidexCustomerUpdateResponseJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		posidexResponseService.execute(context);
		logger.debug(Literal.LEAVING);
	}

	public static void setPosidexResponseService(PosidexResponseServiceImpl posidexResponseService) {
		PosidexCustomerUpdateResponseJob.posidexResponseService = posidexResponseService;
	}

}
