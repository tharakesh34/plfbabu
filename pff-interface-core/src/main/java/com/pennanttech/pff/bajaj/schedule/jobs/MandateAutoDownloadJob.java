package com.pennanttech.pff.bajaj.schedule.jobs;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.MandateProcess;

public class MandateAutoDownloadJob implements Job, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MasterExtractSchedulerJob.class);

	public MandateAutoDownloadJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		MandateProcess mandateProcess;
		try {
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			mandateProcess = (MandateProcess) dataMap.get("mandateProcess");

			mandateProcess.updateMandateStatus();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}
}