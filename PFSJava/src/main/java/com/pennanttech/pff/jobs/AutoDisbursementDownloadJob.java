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
import com.pennanttech.pff.external.disbursement.DisbursementRequestService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoDisbursementDownloadJob implements Job, Serializable {
	private static final long serialVersionUID = -4392973710764138834L;
	private static final Logger logger = LogManager.getLogger(AutoDisbursementDownloadJob.class);

	public static final String JOB_ENABLED = SMTParameterConstants.DISBURSEMENT_AUTO_DOWNLOAD_JOB_ENABLED;
	public static final String JOB_KEY = "AUTO_DISBURSEMENT_DOWNLOAD_JOB";
	public static final String JOB_KEY_DESCRIPTION = "Auto_Disbursement_Download Process";
	public static final String JOB_TRIGGER = "DISBURSEMENT_DOWNLOAD_JOB_TRIGGER";
	private static final String DEFAULT_JOB_FREQUENCY = "0 0/5 * 1/1 * ? *";
	public static final String JOB_FREQUENCY = SMTParameterConstants.DISBURSEMENT_AUTO_DOWNLOAD_JOB_FREQUENCY;

	private static DisbursementRequestService disbursementRequestService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getKey().getName();
		logger.debug(String.format("JOB: %s", jobName));

		if (SysParamUtil.isAllowed(JOB_ENABLED)) {
			getDisbursementRequestService(context).processInstructions();
		} else {
			logger.warn("{} Auto Disbursement job process disabled", JOB_ENABLED);
		}

	}

	private DisbursementRequestService getDisbursementRequestService(JobExecutionContext context) {
		if (disbursementRequestService == null) {
			disbursementRequestService = (DisbursementRequestService) context.getJobDetail().getJobDataMap()
					.get("disbursementRequestService");
		}
		return disbursementRequestService;
	}

	public static String getCronExpression() {
		String cronExpression = SysParamUtil.getValueAsString(JOB_FREQUENCY);

		if (StringUtils.isEmpty(cronExpression)) {
			cronExpression = DEFAULT_JOB_FREQUENCY;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for auto disbursement process not valid.", cronExpression));
		}

		return cronExpression;
	}
}
