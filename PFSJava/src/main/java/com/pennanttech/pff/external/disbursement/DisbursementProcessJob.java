package com.pennanttech.pff.external.disbursement;

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

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DisbursementProcessJob implements Job, Serializable {
	private final Logger logger = LogManager.getLogger(DisbursementProcessJob.class);
	private static final long serialVersionUID = 1L;

	public static final String JOB_ENABLED = SMTParameterConstants.DISBURSEMENT_PROCESS_JOB_ENABLED;
	public static final String JOB_KEY = "DISBURSEMENT_PROCESS_JOB";
	public static final String JOB_KEY_DESCRIPTION = "Disbursement Process";
	public static final String JOB_TRIGGER = "DISBURSEMENT_PROCESS_JOB_TRIGGER";
	private static final String DEFAULT_JOB_FREQUENCY = "0 0/3 * 1/1 * ? *";
	private static final String JOB_FREQUENCY = SMTParameterConstants.DISBURSEMENT_PROCESS_JOB_FREQUENCY;

	private static DisbursementRequestService disbursementRequestService;

	public DisbursementProcessJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (SysParamUtil.isAllowed(JOB_ENABLED)) {
			getDisbursementRequestService(context).processRequests();
		} else {
			logger.warn("{}  process is disabled", "DISBURSEMENT_PROCESS_JOB_ENABLED");
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
					String.format("The cron expression %s for disbursement process not valid.", cronExpression));
		}

		return cronExpression;
	}
}
