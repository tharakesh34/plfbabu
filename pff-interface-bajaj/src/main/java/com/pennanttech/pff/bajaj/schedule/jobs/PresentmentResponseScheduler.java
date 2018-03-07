package com.pennanttech.pff.bajaj.schedule.jobs;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.PresentmentProcess;

public class PresentmentResponseScheduler implements Job, Serializable {
	private static final Logger logger = Logger.getLogger(PresentmentResponseScheduler.class);
	private static final long serialVersionUID = 1L;

	public PresentmentResponseScheduler() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		PresentmentProcess presementProcess;
		Object isJobRequired;
		try {
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();

			presementProcess = (PresentmentProcess) dataMap.get("presementProcess");
			isJobRequired = (Object) dataMap.get("IsJobRequired");
			if (isJobRequired != null && "Y".equals(isJobRequired.toString())) {
				presementProcess.receiveResponse();
			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		} finally {
			logger.debug(Literal.LEAVING);
		}

	}
}
