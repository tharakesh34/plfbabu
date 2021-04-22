package com.pennanttech.pff.schedule.jobs;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.PmayProcess;

public class PmayJob implements Job, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PmayJob.class);

	public PmayJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) {
		PmayProcess pmayProcess;
		try {
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			pmayProcess = (PmayProcess) dataMap.get("pmayProcess");
			if (!SysParamUtil.isAllowed("IS_PMAY_RESPONSE_JOB_REQ")) {
				return;
			}
			pmayProcess.processPmayResponse();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}
}