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
import com.pennanttech.pff.external.mandate.DefaultMandateProcess;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoMandateUploadJob implements Job, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5701320979597722338L;

	private static final Logger logger = LogManager.getLogger(AutoMandateUploadJob.class);

	public static final String JOB_ENABLED = SMTParameterConstants.MANDATE_AUTO_UPLOAD_JOB_ENABLED;
	public static final String JOB_KEY = "MANDATE_AUTO_UPLOAD_JOB";
	public static final String JOB_KEY_DESCRIPTION = "Mandate Auto Upload";
	public static final String JOB_TRIGGER = "MANDATE_AUTO_UPLOAD_JOB_TRIGGER";
	private static final String DEFAULT_JOB_FREQUENCY = "0 0/5 * 1/1 * ? *";
	public static final String JOB_FREQUENCY = SMTParameterConstants.MANDATE_AUTO_UPLOAD_JOB_FREQUENCY;

	private DefaultMandateProcess mandateProcess;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		String jobName = context.getJobDetail().getKey().getName();
		logger.debug(String.format("JOB: %s", jobName));
		if (SysParamUtil.isAllowed(JOB_ENABLED)) {
			try {
				getMandateProcess(context)
						.processAutoResponseFiles(context.getJobDetail().getJobDataMap().get("job").toString());
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		} else {
			logger.warn("Auto Upload of Mandates job disabled");
		}

		logger.debug(Literal.LEAVING);
	}

	private DefaultMandateProcess getMandateProcess(JobExecutionContext context) {
		if (mandateProcess == null) {
			mandateProcess = (DefaultMandateProcess) context.getJobDetail().getJobDataMap().get("mandateProcesses");
		}
		return mandateProcess;
	}

	public static String getCronExpression() {
		String cronExpression = SysParamUtil.getValueAsString(JOB_FREQUENCY);
		if (StringUtils.isEmpty(cronExpression)) {
			cronExpression = DEFAULT_JOB_FREQUENCY;
		}
		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for mandate upload process is not valid.", cronExpression));
		}
		return cronExpression;
	}

	public DefaultMandateProcess getMandateProcess() {
		return mandateProcess;
	}

	public void setMandateProcess(DefaultMandateProcess mandateProcess) {
		this.mandateProcess = mandateProcess;
	}

}
