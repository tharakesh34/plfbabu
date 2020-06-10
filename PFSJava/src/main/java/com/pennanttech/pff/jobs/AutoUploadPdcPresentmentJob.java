package com.pennanttech.pff.jobs;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.service.financemanagement.impl.PresentmentJobService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class AutoUploadPdcPresentmentJob implements Job, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5686556217810115247L;
	private static final Logger logger = LogManager.getLogger(AutoUploadPdcPresentmentJob.class);

	public static final String JOB_ENABLED = SMTParameterConstants.PRESENTMENT_PDC_AUTO_UPLOAD_JOB_ENABLED;
	public static final String JOB_KEY = "PRESENTMENT_PDC_AUTO_UPLOAD_JOB_ENABLED";
	public static final String JOB_KEY_DESCRIPTION = "Presentment PDC Auto Upload Job Enabled";
	public static final String JOB_TRIGGER = "PRESENTMENT_PDC_AUTO_UPLOAD_JOB_TRIGGER";
	private static final String DEFAULT_JOB_FREQUENCY = "0 0/9 * 1/1 * ? *";
	public static final String JOB_FREQUENCY = SMTParameterConstants.PRESENTMENT_PDC_AUTO_UPLOAD_JOB_FREQUENCY;

	private PresentmentJobService jobService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getKey().getName();
		logger.debug(String.format("JOB: %s", jobName));
		if (SysParamUtil.isAllowed(JOB_ENABLED)) {
			try {
				getUploadService(context).uploadPresentment(context.getJobDetail().getJobDataMap().get("job").toString());
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		} else {
			logger.warn("{} Presentment PDC Auto Upload job is disabled", JOB_ENABLED);
		}

	}

	private PresentmentJobService getUploadService(JobExecutionContext context) {
		if (jobService == null) {
			jobService = (PresentmentJobService) context.getJobDetail().getJobDataMap().get("presentmentJobService");
		}
		return jobService;
	}

	public static String getCronExpression() {
		String cronExpression = SysParamUtil.getValueAsString(JOB_FREQUENCY);

		if (StringUtils.isEmpty(cronExpression)) {
			cronExpression = DEFAULT_JOB_FREQUENCY;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for presentment auto upload is not valid.", cronExpression));
		}

		return cronExpression;
	}
}
