package com.pennanttech.pff.jobs;

import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.service.finance.lmsservicelog.impl.LMSServiceLogAlerts;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LMSServiceLogAlertsJob implements Job {
	static final String JOB_KEY = "LMS_Service_Log_ALERTS_JOB";
	static final String JOB_TRIGGER = "LMS_Service_Log_ALERTS_JOB_TRIGGER";
	static final String CRON_EXPRESSION = "0 0/5 * * * ?";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		String lmsServiceLogReq = SysParamUtil.getValueAsString(SMTParameterConstants.LMS_SERVICE_LOG_REQ);
		if (!StringUtils.equals(lmsServiceLogReq, PennantConstants.YES)) {
			return;
		}
		
		LMSServiceLogAlerts alerts = SpringBeanUtil.getBean(LMSServiceLogAlerts.class);
		alerts.sendAlerts();
	}
}
