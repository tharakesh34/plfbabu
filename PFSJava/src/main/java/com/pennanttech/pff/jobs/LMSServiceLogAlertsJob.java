package com.pennanttech.pff.jobs;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.service.finance.lmsservicelog.impl.LMSServiceLogAlerts;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LMSServiceLogAlertsJob implements Job {
	private static final Logger logger = LogManager.getLogger(LMSServiceLogAlertsJob.class);

	static final String JOB_KEY = "LMS_Service_Log_ALERTS_JOB";
	static final String JOB_TRIGGER = "LMS_Service_Log_ALERTS_JOB_TRIGGER";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		String lmsServiceLogReq = SysParamUtil.getValueAsString(SMTParameterConstants.LMS_SERVICE_LOG_REQ);
		if (!StringUtils.equals(lmsServiceLogReq, PennantConstants.YES)) {
			logger.debug("LMS_SERVICE_LOG_REQ parameter value :" + lmsServiceLogReq);
			return;
		}

		LMSServiceLogAlerts alerts = SpringBeanUtil.getBean(LMSServiceLogAlerts.class);
		alerts.sendAlerts();
		logger.debug(Literal.LEAVING);
	}
}
