package com.pennanttech.pff.jobs;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.service.ExternalInterfaceService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoMandateDownloadJob implements Job, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 529131396515710422L;

	private final Logger logger = LogManager.getLogger(AutoMandateDownloadJob.class);

	public static final String JOB_ENABLED = SMTParameterConstants.MANDATE_AUTO_DOWNLOAD_JOB_ENABLED;
	public static final String JOB_KEY = "MANDATE_AUTO_DOWNLOAD_JOB";
	public static final String JOB_KEY_DESCRIPTION = "Mandate Auto Download";
	public static final String JOB_TRIGGER = "MANDATE_AUTO_DOWNLOAD_JOB_TRIGGER";
	private static final String DEFAULT_JOB_FREQUENCY = "0 0/5 * 1/1 * ? *";
	public static final String JOB_FREQUENCY = SMTParameterConstants.MANDATE_AUTO_DOWNLOAD_JOB_FREQUENCY;

	private ExternalInterfaceService externalInterfaceService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		String jobName = context.getJobDetail().getKey().getName();
		logger.debug(String.format("JOB: %s", jobName));
		if (SysParamUtil.isAllowed(JOB_ENABLED)) {
			try {
				getExternalInterfaceService(context).processAutoMandateRequest();
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		} else {
			logger.warn("Auto Upload of Mandates job disabled");
		}

		logger.debug(Literal.LEAVING);

	}

	private ExternalInterfaceService getExternalInterfaceService(JobExecutionContext context) {
		if (externalInterfaceService == null) {
			externalInterfaceService = (ExternalInterfaceService) context.getJobDetail().getJobDataMap()
					.get("externalInterfaceServiceTarget");
		}
		return externalInterfaceService;
	}

	public static String getCronExpression() {
		String cronExpression = SysParamUtil.getValueAsString(JOB_FREQUENCY);

		if (StringUtils.isEmpty(cronExpression)) {
			cronExpression = DEFAULT_JOB_FREQUENCY;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for mandate process not valid.", cronExpression));
		}

		return cronExpression;
	}

}
