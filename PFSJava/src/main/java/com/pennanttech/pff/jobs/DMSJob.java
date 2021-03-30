package com.pennanttech.pff.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.service.DMSService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DMSJob implements Job {
	private static Logger logger = LogManager.getLogger(DMSJob.class);

	public static final String JOB_KEY = "DMS_JOB";
	public static final String JOB_KEY_DESCRIPTION = "File Based Document Management System";
	public static final String JOB_TRIGGER = "DMS_JOB_TRIGGER";
	private static final String DEFAULT_JOB_FREQUENCY = "0 0/1 * 1/1 * ? *";

	private DMSService dMSService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			getDMSService(context).processDocuments();
		} catch (Exception e) {
			logger.debug(Literal.LEAVING);
		}
	}

	public static String getCronExpression() {
		String cronExpression = App.getProperty("dms.job.cron.expression");

		if (StringUtils.isEmpty(cronExpression)) {
			cronExpression = DEFAULT_JOB_FREQUENCY;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(String.format(
					"The cron expression %s for File Based Document Management System not valid.", cronExpression));
		}

		return cronExpression;
	}

	private DMSService getDMSService(JobExecutionContext context) {
		if (dMSService == null) {
			dMSService = (DMSService) context.getJobDetail().getJobDataMap().get("dMSService");
		}
		return dMSService;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

}
